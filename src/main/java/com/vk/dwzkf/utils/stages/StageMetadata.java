package com.vk.dwzkf.utils.stages;

public class StageMetadata {
    private final String stageName;
    private final String stageObjectName;

    public StageMetadata(String stageName, String stageObjectName) {
        this.stageName = stageName;
        this.stageObjectName = stageObjectName;
    }

    @Override
    public String toString() {
        return "StageMetadata{" +
                "stageName='" + stageName + '\'' +
                ", stageObjectName='" + stageObjectName + '\'' +
                '}';
    }

    public static StageMetadata empty() {
        return new StageMetadata("unnamed stage", null);
    }
}
