package com.example.chatbot1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbot1.kb.KnowledgeBase;
import com.example.chatbot1.model.ChatMessage;
import com.example.chatbot1.model.ChatSession;
import com.example.chatbot1.multimodal.Attachment;
import com.example.chatbot1.multimodal.ImageProcessor;
import com.example.chatbot1.multimodal.MultimodalProcessor;
import com.example.chatbot1.multimodal.PdfDocExtractor;
import com.example.chatbot1.multimodal.SpeechManager;
import com.example.chatbot1.nlp.ChatHistoryRepository;
import com.example.chatbot1.nlp.ConversationManager;
import com.example.chatbot1.nlp.QueryAnalyzer;
import com.example.chatbot1.nlp.ResponseManager;
import com.example.chatbot1.ui.ChatAdapter;
import com.example.chatbot1.ui.CodeWorkspaceDialog;
import com.example.chatbot1.ui.HistoryDialog;
import com.example.chatbot1.ui.LibraryDialog;
import com.example.chatbot1.ui.ProjectsDialog;
import com.example.chatbot1.ui.TasksDialog;
import com.example.chatbot1.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ChatAdapter.OnSuggestionClickListener {

    private DrawerLayout drawerLayout;
    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageView btnMic;
    private LinearLayout bannerThinking;

    private MaterialCardView cardAttachmentPreview;
    private ImageView ivAttachmentIcon;
    private TextView tvAttachmentName;
    private TextView tvAttachmentSize;

    private LinearLayout layoutRecentsList;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    // ChatGPT-Style Single Source of Truth Repository & Active Session State
    private ChatHistoryRepository chatHistoryRepository;
    private ChatSession activeSession = null;

    private KnowledgeBase knowledgeBase;
    private ConversationManager conversationManager;
    private QueryAnalyzer queryAnalyzer;
    private ResponseManager responseManager;
    private MultimodalProcessor multimodalProcessor;

    private ImageProcessor imageProcessor;
    private PdfDocExtractor pdfDocExtractor;
    private SpeechManager speechManager;

    private Attachment currentAttachment;
    private Uri cameraPhotoUri;

    private Handler mainHandler;
    private boolean isProcessing = false;
    private boolean isThinkingModeOn = false;

    // Activity Result Launchers
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<String> pickImageLauncher;
    private ActivityResultLauncher<String[]> pickDocLauncher;

    private ActivityResultLauncher<String> requestCameraPermissionLauncher;
    private ActivityResultLauncher<String> requestAudioPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Position bottom section directly above soft keyboard (keypad) when it opens
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainContent), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());

            int bottomPadding = Math.max(systemBars.bottom, ime.bottom);
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, bottomPadding);

            if (ime.bottom > 0 && messageList != null && !messageList.isEmpty() && rvChat != null) {
                rvChat.post(() -> rvChat.smoothScrollToPosition(messageList.size() - 1));
            }

            return insets;
        });

        mainHandler = new Handler(Looper.getMainLooper());

        // Handle Back Gesture/Button with OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        // Initialize NLP & Multimodal Engine
        initNLPEngine();

        // Register Activity Result Launchers
        initActivityLaunchers();

        // Initialize UI Views
        initViews();

        // Setup Sidebar Drawer Navigation
        setupSidebarNavigation();

        // Setup Initial Welcome Card or Load Last Session
        initSessionStateOnStart();
    }

    private void initNLPEngine() {
        knowledgeBase = new KnowledgeBase();
        conversationManager = new ConversationManager();
        queryAnalyzer = new QueryAnalyzer(knowledgeBase);
        responseManager = new ResponseManager();
        multimodalProcessor = new MultimodalProcessor(queryAnalyzer, responseManager);

        chatHistoryRepository = new ChatHistoryRepository(this);
        imageProcessor = new ImageProcessor();
        pdfDocExtractor = new PdfDocExtractor();
        speechManager = new SpeechManager();
    }

    private void initActivityLaunchers() {
        takePictureLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
            if (Boolean.TRUE.equals(success) && cameraPhotoUri != null) {
                setAttachment(cameraPhotoUri, Attachment.Type.IMAGE, "Camera_Photo.jpg", getUriSize(cameraPhotoUri));
            }
        });

        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                setAttachment(uri, Attachment.Type.IMAGE, getFileName(uri), getUriSize(uri));
            }
        });

        pickDocLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                String fileName = getFileName(uri);
                Attachment.Type type = (fileName != null && fileName.toLowerCase().endsWith(".pdf"))
                        ? Attachment.Type.PDF : Attachment.Type.DOCUMENT;
                setAttachment(uri, type, fileName, getUriSize(uri));
            }
        });

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (Boolean.TRUE.equals(isGranted)) {
                        launchCamera();
                    } else {
                        Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                    }
                });

        requestAudioPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (Boolean.TRUE.equals(isGranted)) {
                        startVoiceRecognition();
                    } else {
                        Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        bannerThinking = findViewById(R.id.bannerThinking);

        ImageButton btnSend = findViewById(R.id.btnSend);
        ImageButton btnAddAttachment = findViewById(R.id.btnAddAttachment);
        btnMic = findViewById(R.id.btnMic);

        ImageView btnSidebar = findViewById(R.id.btnSidebar);
        ImageView btnMenu = findViewById(R.id.btnMenu);

        // Preview views
        cardAttachmentPreview = findViewById(R.id.cardAttachmentPreview);
        ivAttachmentIcon = findViewById(R.id.ivAttachmentIcon);
        tvAttachmentName = findViewById(R.id.tvAttachmentName);
        tvAttachmentSize = findViewById(R.id.tvAttachmentSize);
        ImageView btnRemoveAttachment = findViewById(R.id.btnRemoveAttachment);

        layoutRecentsList = findViewById(R.id.layoutRecentsList);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messageList, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatAdapter);

        if (btnSidebar != null) {
            btnSidebar.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> showAttachmentBottomSheet());
        }

        if (btnSend != null) {
            btnSend.setOnClickListener(v -> sendMessageFromInput());
        }

        if (btnAddAttachment != null) {
            btnAddAttachment.setOnClickListener(v -> showAttachmentBottomSheet());
        }

        if (btnRemoveAttachment != null) {
            btnRemoveAttachment.setOnClickListener(v -> removeAttachment());
        }

        if (etMessage != null) {
            etMessage.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessageFromInput();
                    return true;
                }
                return false;
            });

            etMessage.setOnClickListener(v -> {
                if (messageList != null && !messageList.isEmpty() && rvChat != null) {
                    rvChat.postDelayed(() -> rvChat.smoothScrollToPosition(messageList.size() - 1), 200);
                }
            });
        }

        if (btnMic != null) {
            btnMic.setOnClickListener(v -> checkAudioPermissionAndStart());
        }
    }

    private void setupSidebarNavigation() {
        ImageView btnCloseSidebar = findViewById(R.id.btnCloseSidebar);
        if (btnCloseSidebar != null) {
            btnCloseSidebar.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));
        }

        View btnNewChatSidebar = findViewById(R.id.btnNewChatSidebar);
        if (btnNewChatSidebar != null) {
            btnNewChatSidebar.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                startNewChat();
            });
        }

        View btnSidebarHistory = findViewById(R.id.btnSidebarHistory);
        if (btnSidebarHistory != null) {
            btnSidebarHistory.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                showHistoryDialog();
            });
        }

        View btnSidebarImages = findViewById(R.id.btnSidebarImages);
        if (btnSidebarImages != null) {
            btnSidebarImages.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                pickImageLauncher.launch("image/*");
            });
        }

        View btnSidebarLibrary = findViewById(R.id.btnSidebarLibrary);
        if (btnSidebarLibrary != null) {
            btnSidebarLibrary.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                LibraryDialog.show(this, prompt -> processMultimodalQuery(prompt, null));
            });
        }

        View btnSidebarTasks = findViewById(R.id.btnSidebarTasks);
        if (btnSidebarTasks != null) {
            btnSidebarTasks.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                TasksDialog.show(this, task -> processMultimodalQuery(task, null));
            });
        }

        View btnSidebarProjects = findViewById(R.id.btnSidebarProjects);
        if (btnSidebarProjects != null) {
            btnSidebarProjects.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                ProjectsDialog.show(this, projectPrompt -> processMultimodalQuery(projectPrompt, null));
            });
        }

        View btnSidebarCode = findViewById(R.id.btnSidebarCode);
        if (btnSidebarCode != null) {
            btnSidebarCode.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                CodeWorkspaceDialog.show(this, codePrompt -> processMultimodalQuery(codePrompt, null));
            });
        }

        View btnClearHistorySidebar = findViewById(R.id.btnClearHistorySidebar);
        if (btnClearHistorySidebar != null) {
            btnClearHistorySidebar.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                clearAllHistoryDirectly();
            });
        }

        View btnSidebarMore = findViewById(R.id.btnSidebarMore);
        if (btnSidebarMore != null) {
            btnSidebarMore.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                showMoreSettingsDialog();
            });
        }

        ImageView btnRecentsAdd = findViewById(R.id.btnRecentsAdd);
        if (btnRecentsAdd != null) {
            btnRecentsAdd.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                startNewChat();
            });
        }
    }

    private void initSessionStateOnStart() {
        refreshRecentsSidebarUI();
        List<ChatSession> sessions = chatHistoryRepository.getSessions();

        if (!sessions.isEmpty()) {
            openSession(sessions.get(0));
        } else {
            startNewChat();
        }
    }

    private void startNewChat() {
        activeSession = null;
        messageList.clear();
        conversationManager.reset();
        removeAttachment();

        messageList.add(new ChatMessage(ChatMessage.TYPE_WELCOME, "", Utils.getCurrentTimestamp()));
        chatAdapter.notifyDataSetChanged();
        Toast.makeText(this, "New Chat started", Toast.LENGTH_SHORT).show();
    }

    private void openSession(ChatSession session) {
        if (session == null) return;

        activeSession = session;
        messageList.clear();
        conversationManager.reset();
        removeAttachment();

        List<ChatMessage> msgs = session.getMessages();
        if (msgs != null && !msgs.isEmpty()) {
            messageList.addAll(msgs);
        } else {
            messageList.add(new ChatMessage(ChatMessage.TYPE_WELCOME, "", Utils.getCurrentTimestamp()));
        }

        chatAdapter.notifyDataSetChanged();
        if (!messageList.isEmpty()) {
            rvChat.scrollToPosition(messageList.size() - 1);
        }

        refreshRecentsSidebarUI();
    }

    private void refreshRecentsSidebarUI() {
        if (layoutRecentsList == null) return;
        layoutRecentsList.removeAllViews();

        List<ChatSession> sessions = chatHistoryRepository.getSessions();
        for (ChatSession session : sessions) {
            View itemView = LayoutInflater.from(this).inflate(R.layout.item_recent_chat, layoutRecentsList, false);
            TextView tvTitle = itemView.findViewById(R.id.tvRecentTitle);
            ImageView btnOptions = itemView.findViewById(R.id.btnRecentOptions);

            if (tvTitle != null) {
                tvTitle.setText(session.getTitle());
            }

            itemView.setOnClickListener(v -> {
                if (drawerLayout != null) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                openSession(session);
            });

            if (btnOptions != null) {
                btnOptions.setOnClickListener(v -> showSessionOptionsDialog(session));
            }

            layoutRecentsList.addView(itemView);
        }
    }

    private void showSessionOptionsDialog(ChatSession session) {
        if (session == null) return;

        CharSequence[] options = new CharSequence[]{"Rename", "Delete"};
        new AlertDialog.Builder(this)
                .setTitle(session.getTitle())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showRenameSessionDialog(session);
                    } else if (which == 1) {
                        chatHistoryRepository.deleteSession(session.getId());
                        if (activeSession != null && activeSession.getId().equals(session.getId())) {
                            startNewChat();
                        }
                        refreshRecentsSidebarUI();
                        Toast.makeText(this, "Deleted conversation", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void showRenameSessionDialog(ChatSession session) {
        if (session == null) return;

        EditText etInput = new EditText(this);
        etInput.setText(session.getTitle());
        etInput.setSelection(session.getTitle().length());

        new AlertDialog.Builder(this)
                .setTitle("Rename Conversation")
                .setView(etInput)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newTitle = etInput.getText().toString().trim();
                    if (!TextUtils.isEmpty(newTitle)) {
                        chatHistoryRepository.renameSession(session.getId(), newTitle);
                        refreshRecentsSidebarUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showHistoryDialog() {
        List<ChatSession> sessions = chatHistoryRepository.getSessions();
        HistoryDialog.show(this, sessions, new HistoryDialog.OnHistoryActionListener() {
            @Override
            public void onSelectSession(ChatSession session) {
                openSession(session);
            }

            @Override
            public void onDeleteSession(ChatSession session) {
                if (session != null) {
                    chatHistoryRepository.deleteSession(session.getId());
                    if (activeSession != null && activeSession.getId().equals(session.getId())) {
                        startNewChat();
                    }
                    refreshRecentsSidebarUI();
                }
            }

            @Override
            public void onClearAllHistory() {
                clearAllHistoryDirectly();
            }
        });
    }

    private void clearAllHistoryDirectly() {
        chatHistoryRepository.clearAllSessions();
        startNewChat();
        refreshRecentsSidebarUI();
        Toast.makeText(this, "All conversation history cleared", Toast.LENGTH_SHORT).show();
    }

    private void showAttachmentBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_bottom_sheet_attachment, null);

        View btnCamera = view.findViewById(R.id.btnAttachCamera);
        View btnPhotos = view.findViewById(R.id.btnAttachPhotos);
        View btnFiles = view.findViewById(R.id.btnAttachFiles);
        View btnThinking = view.findViewById(R.id.btnAttachThinking);
        TextView tvThinkingState = view.findViewById(R.id.tvThinkingState);

        if (tvThinkingState != null) {
            tvThinkingState.setText(isThinkingModeOn ? "ON" : "OFF");
        }

        if (btnCamera != null) {
            btnCamera.setOnClickListener(v -> {
                dialog.dismiss();
                checkCameraPermissionAndLaunch();
            });
        }

        if (btnPhotos != null) {
            btnPhotos.setOnClickListener(v -> {
                dialog.dismiss();
                pickImageLauncher.launch("image/*");
            });
        }

        if (btnFiles != null) {
            btnFiles.setOnClickListener(v -> {
                dialog.dismiss();
                pickDocLauncher.launch(new String[]{
                        "application/pdf",
                        "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                        "text/plain"
                });
            });
        }

        if (btnThinking != null) {
            btnThinking.setOnClickListener(v -> {
                dialog.dismiss();
                isThinkingModeOn = !isThinkingModeOn;
                bannerThinking.setVisibility(isThinkingModeOn ? View.VISIBLE : View.GONE);
                Toast.makeText(this, isThinkingModeOn ? "Deep Thinking Mode Enabled" : "Deep Thinking Mode Disabled", Toast.LENGTH_SHORT).show();
            });
        }

        dialog.setContentView(view);
        dialog.show();
    }

    private void checkCameraPermissionAndLaunch() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void launchCamera() {
        try {
            File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "camera_photo_" + System.currentTimeMillis() + ".jpg");
            cameraPhotoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            takePictureLauncher.launch(cameraPhotoUri);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAudioPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            startVoiceRecognition();
        } else {
            requestAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO);
        }
    }

    private void startVoiceRecognition() {
        if (speechManager.isListening()) {
            speechManager.stopListening();
            btnMic.clearColorFilter();
            return;
        }

        btnMic.setColorFilter(ContextCompat.getColor(this, R.color.accent_green));

        speechManager.startListening(this, "en-US", new SpeechManager.OnSpeechResultListener() {
            @Override
            public void onReadyForSpeech() {
                Toast.makeText(MainActivity.this, "Listening... Speak now", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onResult(String text) {
                btnMic.clearColorFilter();
                if (etMessage != null && !TextUtils.isEmpty(text)) {
                    etMessage.setText(text);
                    etMessage.setSelection(text.length());
                }
            }

            @Override
            public void onError(String errorMessage) {
                btnMic.clearColorFilter();
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAttachment(Uri uri, Attachment.Type type, String name, long size) {
        currentAttachment = new Attachment(uri, type, name, size);

        tvAttachmentName.setText(name != null ? name : "Attached File");
        tvAttachmentSize.setText(type.name() + " • " + currentAttachment.getFormattedSize());

        if (type == Attachment.Type.IMAGE) {
            try (InputStream is = getContentResolver().openInputStream(uri)) {
                Bitmap bmp = BitmapFactory.decodeStream(is);
                if (bmp != null) {
                    currentAttachment.setThumbnail(bmp);
                    ivAttachmentIcon.setImageBitmap(bmp);
                    ivAttachmentIcon.setPadding(0, 0, 0, 0);
                } else {
                    ivAttachmentIcon.setImageResource(R.drawable.ic_gallery);
                }
            } catch (Exception e) {
                ivAttachmentIcon.setImageResource(R.drawable.ic_gallery);
            }
        } else if (type == Attachment.Type.PDF) {
            ivAttachmentIcon.setImageResource(R.drawable.ic_pdf);
        } else {
            ivAttachmentIcon.setImageResource(R.drawable.ic_doc);
        }

        cardAttachmentPreview.setVisibility(View.VISIBLE);
    }

    private void removeAttachment() {
        currentAttachment = null;
        cardAttachmentPreview.setVisibility(View.GONE);
    }

    private void sendMessageFromInput() {
        if (etMessage == null) return;
        String query = etMessage.getText().toString().trim();

        if (TextUtils.isEmpty(query) && currentAttachment == null) {
            return;
        }

        etMessage.setText("");
        processMultimodalQuery(query, currentAttachment);
        removeAttachment();
    }

    @Override
    public void onSuggestionClick(String suggestion) {
        if (!isProcessing && !TextUtils.isEmpty(suggestion)) {
            processMultimodalQuery(suggestion, null);
        }
    }

    private void processMultimodalQuery(String userQuery, Attachment attachment) {
        if (isProcessing) return;
        isProcessing = true;

        String queryForEngine = userQuery;
        if (isThinkingModeOn && !userQuery.contains("in detail")) {
            queryForEngine = userQuery + " in detail";
        }

        String displayMsg = userQuery;
        if (attachment != null) {
            displayMsg = (userQuery.isEmpty() ? "" : userQuery + "\n\n") + "[📷 Attached: " + attachment.getFileName() + "]";
        }

        // 1. Manage Active ChatSession (Single Source of Truth)
        if (activeSession == null) {
            activeSession = new ChatSession();
            String title = generateTitleFromQuery(userQuery, attachment);
            activeSession.setTitle(title);
        }

        // 2. Add User Message
        ChatMessage userMsg = new ChatMessage(ChatMessage.TYPE_USER, displayMsg, Utils.getCurrentTimestamp());

        // Remove welcome card if present
        if (!messageList.isEmpty() && messageList.get(0).getViewType() == ChatMessage.TYPE_WELCOME) {
            messageList.remove(0);
            chatAdapter.notifyItemRemoved(0);
        }

        messageList.add(userMsg);
        activeSession.addMessage(userMsg);
        chatHistoryRepository.saveOrUpdateSession(activeSession);

        int userPos = messageList.size() - 1;
        chatAdapter.notifyItemInserted(userPos);
        rvChat.smoothScrollToPosition(userPos);

        refreshRecentsSidebarUI();

        // 3. Add Typing Indicator
        String statusText = getLoadingStatusText(attachment);
        ChatMessage typingMsg = new ChatMessage(ChatMessage.TYPE_TYPING, statusText, Utils.getCurrentTimestamp());
        messageList.add(typingMsg);
        int typingPos = messageList.size() - 1;
        chatAdapter.notifyItemInserted(typingPos);
        rvChat.smoothScrollToPosition(typingPos);

        // 4. Process Attachment Content
        final String finalEngineQuery = queryForEngine;
        if (attachment != null) {
            if (attachment.getType() == Attachment.Type.IMAGE) {
                imageProcessor.processImage(this, attachment.getUri(), new ImageProcessor.OnTextExtractedListener() {
                    @Override
                    public void onSuccess(String extractedText) {
                        attachment.setExtractedText(extractedText);
                        generateAndDisplayBotResponse(finalEngineQuery, attachment);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        attachment.setExtractedText("");
                        generateAndDisplayBotResponse(finalEngineQuery, attachment);
                    }
                });
            } else {
                pdfDocExtractor.extractText(this, attachment.getUri(), getContentResolver().getType(attachment.getUri()), attachment.getFileName(), new PdfDocExtractor.OnExtractionListener() {
                    @Override
                    public void onSuccess(String extractedText) {
                        attachment.setExtractedText(extractedText);
                        generateAndDisplayBotResponse(finalEngineQuery, attachment);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        attachment.setExtractedText("");
                        generateAndDisplayBotResponse(finalEngineQuery, attachment);
                    }
                });
            }
        } else {
            mainHandler.postDelayed(() -> generateAndDisplayBotResponse(finalEngineQuery, null), 500);
        }
    }

    private String generateTitleFromQuery(String query, Attachment attachment) {
        if (!TextUtils.isEmpty(query)) {
            String trimmed = query.trim();
            if (trimmed.length() > 30) {
                return trimmed.substring(0, 30) + "…";
            }
            return trimmed;
        }
        if (attachment != null) {
            return "Attachment: " + attachment.getFileName();
        }
        return "Machine Learning Conversation";
    }

    private String getLoadingStatusText(Attachment attachment) {
        if (isThinkingModeOn) return "🧠 Deep Reasoning & Analyzing...";
        if (attachment == null) return "Thinking...";
        if (attachment.getType() == Attachment.Type.IMAGE) return "Analyzing image & extracting text...";
        if (attachment.getType() == Attachment.Type.PDF) return "Reading PDF document...";
        return "Reading document & extracting text...";
    }

    private void generateAndDisplayBotResponse(String userQuery, Attachment attachment) {
        mainHandler.post(() -> {
            // Remove typing indicator
            if (!messageList.isEmpty() && messageList.get(messageList.size() - 1).getViewType() == ChatMessage.TYPE_TYPING) {
                int removePos = messageList.size() - 1;
                messageList.remove(removePos);
                chatAdapter.notifyItemRemoved(removePos);
            }

            // Process via Multimodal Processor
            ChatMessage botMsg = multimodalProcessor.processQuery(userQuery, attachment, conversationManager);
            messageList.add(botMsg);

            // Persist bot message to active session
            if (activeSession != null) {
                activeSession.addMessage(botMsg);
                chatHistoryRepository.saveOrUpdateSession(activeSession);
                refreshRecentsSidebarUI();
            }

            int botPos = messageList.size() - 1;
            chatAdapter.notifyItemInserted(botPos);
            rvChat.smoothScrollToPosition(botPos);

            isProcessing = false;
        });
    }

    private void showMoreSettingsDialog() {
        CharSequence[] options = new CharSequence[]{
                "⚙️ Settings",
                "📜 Chat History",
                "🆕 New Chat",
                "🧹 Clear All History",
                "❓ Help & FAQ",
                "ℹ️ About ML Assistant"
        };
        new AlertDialog.Builder(this)
                .setTitle("ML Assistant - Options")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 1:
                            showHistoryDialog();
                            break;
                        case 2:
                            startNewChat();
                            break;
                        case 3:
                            clearAllHistoryDirectly();
                            break;
                        default:
                            Toast.makeText(this, options[which] + " selected", Toast.LENGTH_SHORT).show();
                            break;
                    }
                })
                .setNegativeButton("Close", null)
                .show();
    }

    private String getFileName(Uri uri) {
        String result = null;
        if ("content".equals(uri.getScheme())) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int idx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (idx != -1) result = cursor.getString(idx);
                }
            } catch (Exception ignored) {}
        }
        if (result == null) {
            result = uri.getPath();
            if (result != null) {
                int cut = result.lastIndexOf('/');
                if (cut != -1) result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private long getUriSize(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idx = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (idx != -1) return cursor.getLong(idx);
            }
        } catch (Exception ignored) {}
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechManager != null) {
            speechManager.stopListening();
        }
    }
}