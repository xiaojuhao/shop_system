package com.xjh.ws;

import com.xjh.common.utils.Safe;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WsAttachment {
    String requestId;
    Integer deskId;
    Integer accountId;
    String accountUser;

    List<Runnable> next = new ArrayList<>();

    public void addNext(Runnable runnable) {
        next.add(runnable);
    }

    public void drains() {
        for (Runnable r : next) {
            Safe.run(r);
        }
        next.clear();
    }
}
