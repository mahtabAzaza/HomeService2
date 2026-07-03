package entity;

public enum OrderStatus {

    // مشتری سفارش ثبت کرده، منتظر پیشنهاد متخصصان
    WAITING_FOR_PROPOSAL,

    // انتخاب یک پیشنهاد توسط مشتری
        WAITING_FOR_SELECTION,

    // در انتظار رسیدن متخصص
    WAITING_FOR_SPECIALIST,

    // متخصص آمده و کار شروع شده
    IN_PROGRESS,

    // کار تمام شده
    DONE,

    // مشتری پرداخت کرده
    PAID
}
