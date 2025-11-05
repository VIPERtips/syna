package co.zw.blexta.syna.common.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentUserUtils {

    public static Long getCurrentClerkUserId() {
        String clerkUserIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Long.valueOf(clerkUserIdStr);
    }
}
