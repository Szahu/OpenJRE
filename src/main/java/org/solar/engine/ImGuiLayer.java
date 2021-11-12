package org.solar.engine;

import imgui.*;
import imgui.gl3.ImGuiImplGl3;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    private long glfwWindow;

    // Mouse cursors provided by GLFW
    private final long[] mouseCursors = new long[imgui.flag.ImGuiMouseCursor.COUNT];

    // LWJGL3 renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public ImGuiLayer(long glfwWindow) {
        this.glfwWindow = glfwWindow;
    }

    // Initialize Dear ImGui.
    public void initImGui() {
        // IMPORTANT!!
        // This line is critical for Dear ImGui to work.
        ImGui.createContext();

        // ------------------------------------------------------------
        // Initialize ImGuiIO config
        final ImGuiIO io = ImGui.getIO();

        io.setIniFilename(null); // We don't want to save .ini file
        io.setConfigFlags(imgui.flag.ImGuiConfigFlags.NavEnableKeyboard); // Navigation with keyboard
        io.setBackendFlags(imgui.flag.ImGuiBackendFlags.HasMouseCursors); // Mouse cursors to display while resizing windows etc.
        io.setBackendPlatformName("imgui_java_impl_glfw");

        // ------------------------------------------------------------
        // Keyboard mapping. ImGui will use those indices to peek into the io.KeysDown[] array.
        final int[] keyMap = new int[imgui.flag.ImGuiKey.COUNT];
        keyMap[imgui.flag.ImGuiKey.Tab] = GLFW_KEY_TAB;
        keyMap[imgui.flag.ImGuiKey.LeftArrow] = GLFW_KEY_LEFT;
        keyMap[imgui.flag.ImGuiKey.RightArrow] = GLFW_KEY_RIGHT;
        keyMap[imgui.flag.ImGuiKey.UpArrow] = GLFW_KEY_UP;
        keyMap[imgui.flag.ImGuiKey.DownArrow] = GLFW_KEY_DOWN;
        keyMap[imgui.flag.ImGuiKey.PageUp] = GLFW_KEY_PAGE_UP;
        keyMap[imgui.flag.ImGuiKey.PageDown] = GLFW_KEY_PAGE_DOWN;
        keyMap[imgui.flag.ImGuiKey.Home] = GLFW_KEY_HOME;
        keyMap[imgui.flag.ImGuiKey.End] = GLFW_KEY_END;
        keyMap[imgui.flag.ImGuiKey.Insert] = GLFW_KEY_INSERT;
        keyMap[imgui.flag.ImGuiKey.Delete] = GLFW_KEY_DELETE;
        keyMap[imgui.flag.ImGuiKey.Backspace] = GLFW_KEY_BACKSPACE;
        keyMap[imgui.flag.ImGuiKey.Space] = GLFW_KEY_SPACE;
        keyMap[imgui.flag.ImGuiKey.Enter] = GLFW_KEY_ENTER;
        keyMap[imgui.flag.ImGuiKey.Escape] = GLFW_KEY_ESCAPE;
        keyMap[imgui.flag.ImGuiKey.KeyPadEnter] = GLFW_KEY_KP_ENTER;
        keyMap[imgui.flag.ImGuiKey.A] = GLFW_KEY_A;
        keyMap[imgui.flag.ImGuiKey.C] = GLFW_KEY_C;
        keyMap[imgui.flag.ImGuiKey.V] = GLFW_KEY_V;
        keyMap[imgui.flag.ImGuiKey.X] = GLFW_KEY_X;
        keyMap[imgui.flag.ImGuiKey.Y] = GLFW_KEY_Y;
        keyMap[imgui.flag.ImGuiKey.Z] = GLFW_KEY_Z;
        io.setKeyMap(keyMap);

        // ------------------------------------------------------------
        // Mouse cursors mapping
        mouseCursors[imgui.flag.ImGuiMouseCursor.Arrow] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[imgui.flag.ImGuiMouseCursor.TextInput] = glfwCreateStandardCursor(GLFW_IBEAM_CURSOR);
        mouseCursors[imgui.flag.ImGuiMouseCursor.ResizeAll] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[imgui.flag.ImGuiMouseCursor.ResizeNS] = glfwCreateStandardCursor(GLFW_VRESIZE_CURSOR);
        mouseCursors[imgui.flag.ImGuiMouseCursor.ResizeEW] = glfwCreateStandardCursor(GLFW_HRESIZE_CURSOR);
        mouseCursors[imgui.flag.ImGuiMouseCursor.ResizeNESW] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[imgui.flag.ImGuiMouseCursor.ResizeNWSE] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);
        mouseCursors[imgui.flag.ImGuiMouseCursor.Hand] = glfwCreateStandardCursor(GLFW_HAND_CURSOR);
        mouseCursors[imgui.flag.ImGuiMouseCursor.NotAllowed] = glfwCreateStandardCursor(GLFW_ARROW_CURSOR);

        // ------------------------------------------------------------
        // GLFW callbacks to handle user input

        glfwSetKeyCallback(glfwWindow, (w, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) {
                io.setKeysDown(key, true);
            } else if (action == GLFW_RELEASE) {
                io.setKeysDown(key, false);
            }

            io.setKeyCtrl(io.getKeysDown(GLFW_KEY_LEFT_CONTROL) || io.getKeysDown(GLFW_KEY_RIGHT_CONTROL));
            io.setKeyShift(io.getKeysDown(GLFW_KEY_LEFT_SHIFT) || io.getKeysDown(GLFW_KEY_RIGHT_SHIFT));
            io.setKeyAlt(io.getKeysDown(GLFW_KEY_LEFT_ALT) || io.getKeysDown(GLFW_KEY_RIGHT_ALT));
            io.setKeySuper(io.getKeysDown(GLFW_KEY_LEFT_SUPER) || io.getKeysDown(GLFW_KEY_RIGHT_SUPER));
        });

        glfwSetCharCallback(glfwWindow, (w, c) -> {
            if (c != GLFW_KEY_DELETE) {
                io.addInputCharacter(c);
            }
        });

        glfwSetMouseButtonCallback(glfwWindow, (w, button, action, mods) -> {
            final boolean[] mouseDown = new boolean[5];

            mouseDown[0] = button == GLFW_MOUSE_BUTTON_1 && action != GLFW_RELEASE;
            mouseDown[1] = button == GLFW_MOUSE_BUTTON_2 && action != GLFW_RELEASE;
            mouseDown[2] = button == GLFW_MOUSE_BUTTON_3 && action != GLFW_RELEASE;
            mouseDown[3] = button == GLFW_MOUSE_BUTTON_4 && action != GLFW_RELEASE;
            mouseDown[4] = button == GLFW_MOUSE_BUTTON_5 && action != GLFW_RELEASE;

            io.setMouseDown(mouseDown);

            if (!io.getWantCaptureMouse() && mouseDown[1]) {
                ImGui.setWindowFocus(null);
            }
        });

        glfwSetScrollCallback(glfwWindow, (w, xOffset, yOffset) -> {
            io.setMouseWheelH(io.getMouseWheelH() + (float) xOffset);
            io.setMouseWheel(io.getMouseWheel() + (float) yOffset);
        });

        /* io.setSetClipboardTextFn(new ImStrConsumer() {
            @Override
            public void accept(final String s) {
                glfwSetClipboardString(glfwWindow, s);
            }
        }); */

        /* io.setGetClipboardTextFn(new ImStrSupplier() {
            @Override
            public String get() {
                final String clipboardString = glfwGetClipboardString(glfwWindow);
                if (clipboardString != null) {
                    return clipboardString;
                } else {
                    return "";
                }
            }
        });
 */
        // ------------------------------------------------------------
        // Fonts configuration
        // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

//        final ImFontAtlas fontAtlas = io.getFonts();
//        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed
//
//        // Glyphs could be added per-font as well as per config used globally like here
//        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesCyrillic());
//
//        // Add a default font, which is 'ProggyClean.ttf, 13px'
//        fontAtlas.addFontDefault();
//
//        // Fonts merge example
//        fontConfig.setMergeMode(true); // When enabled, all fonts added with this config would be merged with the previously added font
//        fontConfig.setPixelSnapH(true);
//
//        fontAtlas.addFontFromMemoryTTF(loadFromResources("basis33.ttf"), 16, fontConfig);
//
//        fontConfig.setMergeMode(false);
//        fontConfig.setPixelSnapH(false);
//
//        // Fonts from file/memory example
//        // We can add new fonts from the file system
//        fontAtlas.addFontFromFileTTF("src/test/resources/Righteous-Regular.ttf", 14, fontConfig);
//        fontAtlas.addFontFromFileTTF("src/test/resources/Righteous-Regular.ttf", 16, fontConfig);
//
//        // Or directly from the memory
//        fontConfig.setName("Roboto-Regular.ttf, 14px"); // This name will be displayed in Style Editor
//        fontAtlas.addFontFromMemoryTTF(loadFromResources("Roboto-Regular.ttf"), 14, fontConfig);
//        fontConfig.setName("Roboto-Regular.ttf, 16px"); // We can apply a new config value every time we add a new font
//        fontAtlas.addFontFromMemoryTTF(loadFromResources("Roboto-Regular.ttf"), 16, fontConfig);
//
//        fontConfig.destroy(); // After all fonts were added we don't need this config more
//
//        // ------------------------------------------------------------
//        // Use freetype instead of stb_truetype to build a fonts texture
//        ImGuiFreeType.buildFontAtlas(fontAtlas, ImGuiFreeType.RasterizerFlags.LightHinting);

        // Method initializes LWJGL3 renderer.
        // This method SHOULD be called after you've initialized your ImGui configuration (fonts and so on).
        // ImGui context should be created as well.
        imGuiGl3.init("#version 330 core");
    }

    public void update(float dt, Window window, Runnable imGuiCallback) {
        startFrame(dt, window);

        // Any Dear ImGui code SHOULD go between ImGui.newFrame()/ImGui.render() methods
        ImGui.newFrame();
        //ImGui.showDemoWindow();
        imGuiCallback.run();
        ImGui.render();

        endFrame();
    }

    private void startFrame(final float deltaTime, Window window) {
        // Get window properties and mouse position
        float[] winWidth = {window.getWidth()};
        float[] winHeight = {window.getHeight()};
        double[] mousePosX = {0};
        double[] mousePosY = {0};
        glfwGetCursorPos(glfwWindow, mousePosX, mousePosY);

        // We SHOULD call those methods to update Dear ImGui state for the current frame
        final ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(winWidth[0], winHeight[0]);
        io.setDisplayFramebufferScale(1f, 1f);
        io.setMousePos((float) mousePosX[0], (float) mousePosY[0]);
        io.setDeltaTime(deltaTime);

        // Update the mouse cursor
        final int imguiCursor = ImGui.getMouseCursor();
        glfwSetCursor(glfwWindow, mouseCursors[imguiCursor]);
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    private void endFrame() {
        // After Dear ImGui prepared a draw data, we use it in the LWJGL3 renderer.
        // At that moment ImGui will be rendered to the current OpenGL context.
        imGuiGl3.renderDrawData(ImGui.getDrawData());
    }

    // If you want to clean a room after yourself - do it by yourself
    private void destroyImGui() {
        imGuiGl3.dispose();
        ImGui.destroyContext();
    }
}