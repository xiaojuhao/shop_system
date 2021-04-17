package book.enumeration;

public enum EnumDesKStatus {
    FREE(1, "空闲中"),
    USED(2, "使用中"),
    PRESERVED(3, "已预约"),
    PAID(4, "已支付");

    int status;
    String remark;

    public String remark() {
        return remark;
    }

    EnumDesKStatus(int status, String remark) {
        this.status = status;
        this.remark = remark;
    }

    public static EnumDesKStatus of(Integer status) {
        if (status == null) {
            return FREE;
        }
        for (EnumDesKStatus e : EnumDesKStatus.values()) {
            if (e.status == status) {
                return e;
            }
        }
        return FREE;
    }
}
