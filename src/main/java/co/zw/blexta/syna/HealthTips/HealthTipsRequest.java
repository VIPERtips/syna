package co.zw.blexta.syna.HealthTips;

import lombok.Data;

@Data
public class HealthTipsRequest {
    private String location;
    private String season;
    private String weather;
    private String message;
}