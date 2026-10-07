package com.iment.app_mobile_tcc.questions.enums;

public enum QuestionTypeEnum {
    DRAG_DOTS(false, false),
    DRAG_TO_SLOTS(true, false),
    DRAG_LETTERS(true, false),
    DRAG_SLOTS_TO_GROUP(true, true),
    DRAG_SUBTRACTION(false, false);

    private final boolean slotBased;
    private final boolean allowMultiple;

    QuestionTypeEnum(boolean slotBased, boolean allowMultiple){
        this.slotBased = slotBased;
        this.allowMultiple = allowMultiple;
    }

    public boolean isSlotBased(){
        return this.slotBased;
    }

    public boolean isAllowMultiple(){
        return this.allowMultiple;
    }
}
