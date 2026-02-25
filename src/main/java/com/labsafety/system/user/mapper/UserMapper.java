package com.labsafety.system.user.mapper;

import com.labsafety.system.user.User;
import com.labsafety.system.user.dto.UserResponse;

import java.lang.reflect.Method;

public class UserMapper {

    private UserMapper() {}

    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        UserResponse r = new UserResponse();
        r.setId((Long) safeInvoke(user, "getId"));
        r.setUsername((String) safeInvoke(user, "getUsername"));

        Object role = safeInvoke(user, "getRole");
        r.setRole(role == null ? null : String.valueOf(role));

        String phone = asString(safeInvoke(user, "getPhone"));
        String email = asString(safeInvoke(user, "getEmail"));
        String status = asString(safeInvoke(user, "getStatus"));

        // preferred in Chef-O2O
        r.setPhone(phone);

        // keep email for compatibility; fallback to phone if no email
        r.setEmail((email != null && !email.isBlank()) ? email : phone);

        r.setStatus(status);

        return r;
    }

    private static Object safeInvoke(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            return m.invoke(target);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static String asString(Object v) {
        return v == null ? null : String.valueOf(v);
    }
}
