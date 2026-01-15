package de.bungee.uifile.completion;

import java.util.*;

/**
 * Definitions of UI component types and their available properties
 */
public class UITypeDefinitions {

    public static class PropertyInfo {
        public final String name;
        public final String description;
        public final String valueType;

        public PropertyInfo(String name, String description, String valueType) {
            this.name = name;
            this.description = description;
            this.valueType = valueType;
        }
    }

    private static final Map<String, List<PropertyInfo>> UI_TYPES = new HashMap<>();

    static {
        // Common properties for all UI components
        List<PropertyInfo> commonProps = Arrays.asList(
            new PropertyInfo("Background", "Background color of the component", "color"),
            new PropertyInfo("Style", "Styling properties", "style block"),
            new PropertyInfo("Padding", "Padding around the component", "padding value"),
            new PropertyInfo("Margin", "Margin around the component", "margin value"),
            new PropertyInfo("Width", "Width of the component", "number"),
            new PropertyInfo("Height", "Height of the component", "number"),
            new PropertyInfo("Visible", "Visibility of the component", "boolean"),
            new PropertyInfo("Enabled", "Whether the component is enabled", "boolean")
        );

        // Group component
        List<PropertyInfo> groupProps = new ArrayList<>(commonProps);
        groupProps.addAll(Arrays.asList(
            new PropertyInfo("Anchor", "Anchor point and dimensions", "anchor value"),
            new PropertyInfo("Layout", "Layout type for children", "layout value"),
            new PropertyInfo("Spacing", "Spacing between children", "number")
        ));
        UI_TYPES.put("Group", groupProps);

        // Label component
        List<PropertyInfo> labelProps = new ArrayList<>(commonProps);
        labelProps.addAll(Arrays.asList(
            new PropertyInfo("Text", "Text content of the label", "string"),
            new PropertyInfo("TextAlign", "Text alignment", "alignment value"),
            new PropertyInfo("TextColor", "Color of the text", "color"),
            new PropertyInfo("FontSize", "Font size", "number"),
            new PropertyInfo("FontWeight", "Font weight", "weight value"),
            new PropertyInfo("WordWrap", "Enable word wrapping", "boolean")
        ));
        UI_TYPES.put("Label", labelProps);

        // Button component
        List<PropertyInfo> buttonProps = new ArrayList<>(commonProps);
        buttonProps.addAll(Arrays.asList(
            new PropertyInfo("Text", "Text on the button", "string"),
            new PropertyInfo("TextColor", "Color of the button text", "color"),
            new PropertyInfo("HoverBackground", "Background color on hover", "color"),
            new PropertyInfo("PressedBackground", "Background color when pressed", "color"),
            new PropertyInfo("DisabledBackground", "Background color when disabled", "color"),
            new PropertyInfo("BorderRadius", "Border radius", "number"),
            new PropertyInfo("BorderColor", "Border color", "color"),
            new PropertyInfo("BorderWidth", "Border width", "number"),
            new PropertyInfo("OnClick", "Click event handler", "event handler")
        ));
        UI_TYPES.put("Button", buttonProps);

        // TextField / TextInput component
        List<PropertyInfo> textFieldProps = new ArrayList<>(commonProps);
        textFieldProps.addAll(Arrays.asList(
            new PropertyInfo("Text", "Default text value", "string"),
            new PropertyInfo("PlaceholderText", "Placeholder text", "string"),
            new PropertyInfo("TextColor", "Color of the text", "color"),
            new PropertyInfo("PlaceholderColor", "Color of placeholder text", "color"),
            new PropertyInfo("MaxLength", "Maximum text length", "number"),
            new PropertyInfo("ReadOnly", "Whether the field is read-only", "boolean"),
            new PropertyInfo("Password", "Password input mode", "boolean"),
            new PropertyInfo("BorderColor", "Border color", "color"),
            new PropertyInfo("BorderWidth", "Border width", "number"),
            new PropertyInfo("BorderRadius", "Border radius", "number"),
            new PropertyInfo("OnChange", "Change event handler", "event handler")
        ));
        UI_TYPES.put("TextField", textFieldProps);
        UI_TYPES.put("TextInput", textFieldProps);

        // Image component
        List<PropertyInfo> imageProps = new ArrayList<>(commonProps);
        imageProps.addAll(Arrays.asList(
            new PropertyInfo("Source", "Image source path", "string"),
            new PropertyInfo("Stretch", "Image stretch mode", "stretch value"),
            new PropertyInfo("AspectRatio", "Maintain aspect ratio", "boolean"),
            new PropertyInfo("Tint", "Image tint color", "color")
        ));
        UI_TYPES.put("Image", imageProps);

        // CheckBox component
        List<PropertyInfo> checkBoxProps = new ArrayList<>(commonProps);
        checkBoxProps.addAll(Arrays.asList(
            new PropertyInfo("Text", "Label text", "string"),
            new PropertyInfo("Checked", "Whether checked by default", "boolean"),
            new PropertyInfo("TextColor", "Color of the text", "color"),
            new PropertyInfo("CheckColor", "Color of the check mark", "color"),
            new PropertyInfo("OnChange", "Change event handler", "event handler")
        ));
        UI_TYPES.put("CheckBox", checkBoxProps);

        // Slider component
        List<PropertyInfo> sliderProps = new ArrayList<>(commonProps);
        sliderProps.addAll(Arrays.asList(
            new PropertyInfo("Value", "Current value", "number"),
            new PropertyInfo("MinValue", "Minimum value", "number"),
            new PropertyInfo("MaxValue", "Maximum value", "number"),
            new PropertyInfo("Step", "Step increment", "number"),
            new PropertyInfo("TrackColor", "Color of the track", "color"),
            new PropertyInfo("ThumbColor", "Color of the thumb", "color"),
            new PropertyInfo("OnChange", "Change event handler", "event handler")
        ));
        UI_TYPES.put("Slider", sliderProps);

        // Panel component
        List<PropertyInfo> panelProps = new ArrayList<>(commonProps);
        panelProps.addAll(Arrays.asList(
            new PropertyInfo("BorderColor", "Border color", "color"),
            new PropertyInfo("BorderWidth", "Border width", "number"),
            new PropertyInfo("BorderRadius", "Border radius", "number"),
            new PropertyInfo("ScrollEnabled", "Enable scrolling", "boolean")
        ));
        UI_TYPES.put("Panel", panelProps);

        // ScrollView component
        List<PropertyInfo> scrollViewProps = new ArrayList<>(commonProps);
        scrollViewProps.addAll(Arrays.asList(
            new PropertyInfo("ScrollbarVisible", "Show scrollbar", "boolean"),
            new PropertyInfo("ScrollbarColor", "Scrollbar color", "color"),
            new PropertyInfo("HorizontalScroll", "Enable horizontal scroll", "boolean"),
            new PropertyInfo("VerticalScroll", "Enable vertical scroll", "boolean")
        ));
        UI_TYPES.put("ScrollView", scrollViewProps);
    }

    /**
     * Get all available UI component types
     */
    public static Set<String> getUITypes() {
        return UI_TYPES.keySet();
    }

    /**
     * Get properties for a specific UI component type
     */
    public static List<PropertyInfo> getPropertiesForType(String type) {
        return UI_TYPES.getOrDefault(type, Collections.emptyList());
    }

    /**
     * Check if a type exists
     */
    public static boolean isValidType(String type) {
        return UI_TYPES.containsKey(type);
    }
}

