package edu.nccu.domain;

import java.util.Objects;

public enum ModuleState {
    ENABLED,
    DISABLED;

    public static ModuleState ofConfig(Boolean enabledConfig) {
        if (Objects.isNull(enabledConfig)) {
            return DISABLED;
        } else if (enabledConfig) {
            return ENABLED;
        } else {
            return DISABLED;
        }
    }
}
