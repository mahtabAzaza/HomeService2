package entity;

public enum OrderStatus {

    // مشتری سفارش ثبت کرده، منتظر پیشنهاد متخصصان
    WAITING_FOR_PROPOSAL,

    // مشتری یک پیشنهاد را انتخاب کرده، منتظر آمدن متخصص
    WAITING_FOR_SPECIALIST_TO_COME,

    // متخصص آمده و کار شروع شده
    IN_PROGRESS,

    // کار تمام شده
    DONE,

    // مشتری پرداخت کرده
    PAID
}
