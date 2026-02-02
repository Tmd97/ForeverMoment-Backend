package com.example.moment_forever.core.admin.dto;

public class CategoryDto extends NamedEntityDto {
    private String desc;
    private boolean isEnabled;


    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
