package io.github.ultimateboomer.resolutioncontrol;

public class ResolutionControlMod {
    private static ResolutionControlMod instance;

    // Hold an instance of the new class
    private static final cc.modlabs.resolutioncontrol.ResolutionControlMod newModInstance =
            cc.modlabs.resolutioncontrol.ResolutionControlMod.getInstance();

    // Public method to get the old class instance, but internally it uses the new class.
    // This returns the old type for compatibility but internally uses the new type.
    public static ResolutionControlMod getInstance() {
        if (instance == null) {
            instance = new ResolutionControlMod();
        }
        return instance;
    }

    public void onResolutionChanged() {
        newModInstance.onResolutionChanged();
    }
}