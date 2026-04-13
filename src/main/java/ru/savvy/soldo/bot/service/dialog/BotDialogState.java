package ru.savvy.soldo.bot.service.dialog;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Состояние диалога бронирования для одного Telegram-чата.
 * Хранится в памяти (см. {@link BotDialogStateService}).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotDialogState {
    @Builder.Default
    private BotDialogStep step = BotDialogStep.IDLE;

    private Long selectedCategoryId;
    private Long selectedEventId;
    private String guestName;
    private String guestPhone;

    public void reset() {
        this.step = BotDialogStep.IDLE;
        this.selectedCategoryId = null;
        this.selectedEventId = null;
        this.guestName = null;
        this.guestPhone = null;
    }
}
