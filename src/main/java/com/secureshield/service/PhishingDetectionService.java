package com.secureshield.service;

import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
public class PhishingDetectionService {
    
    @Value("${app.phishing.blacklist.enabled:true}")
    private boolean blacklistEnabled;
    
    @Value("${app.phishing.heuristics.max-url-length:100}")
    private int maxUrlLength;
    
    @Value("${app.phishing.heuristics.suspicious-tlds:tk,ml,ga,cf,gq,xyz,top}")
    private List<String> suspiciousTlds;
    
    private final Set<String> phishingBlacklist = new HashSet<>(Arrays.asList(
        "paypa1.com", "goog1e.com", "amaxon.com", "microsoftsupport.com",
        "bankofamerica-secure.com", "netfl1x.com", "update-windows.com",
        "0.0.0.0", "127.0.0.1", "localhost", "169.254.169.254"
    ));
    
    private final Pattern homoglyphPattern = Pattern.compile("[ᵢḭïîìí]|rn|lv|o0|cl|Ð|Ñ|Ò|Ó|Ô|Õ|Ö|Ø|ő|ð|đ");
    private final Pattern ipPattern = Pattern.compile("^https?://(?:[0-9]{1,3}\\.){3}[0-9]{1,3}");
    private final Pattern suspiciousPath = Pattern.compile("(login|secure|update|verify|account|banking|payment)[/]|\\.php\\?|\\.asp\\?", Pattern.CASE_INSENSITIVE);
    
    public PhishingResult checkUrl(String urlStr) {
        try {
            String fullUrl = urlStr.startsWith("http") ? urlStr : "https://" + urlStr;
            URL url = new URL(fullUrl);
            String host = url.getHost().toLowerCase();
            
            PhishingResult result = new PhishingResult();
            result.setUrl(fullUrl);
            
            int score = 0;
            List<String> reasons = new ArrayList<>();
            
            // Check 1: Blacklist
            if (blacklistEnabled && phishingBlacklist.stream().anyMatch(host::contains)) {
                reasons.add("Known phishing domain");
                score = 100;
            }
            
            // Check 2: Suspicious TLD
            String tld = host.substring(host.lastIndexOf(".") + 1);
            if (suspiciousTlds.contains(tld)) {
                reasons.add("Suspicious TLD: " + tld);
                score += 25;
            }
            
            // Check 3: IP address in URL
            if (ipPattern.matcher(fullUrl).find()) {
                reasons.add("IP address in URL (phishing indicator)");
                score += 30;
            }
            
            // Check 4: Homoglyphs/IDN
            if (homoglyphPattern.matcher(host).find()) {
                reasons.add("Homoglyph characters detected");
                score += 20;
            }
            
            // Check 5: Excessive length/random subdomains
            if (host.length() > maxUrlLength) {
                reasons.add("Excessive URL length");
                score += 15;
            }
            
            // Check 6: Suspicious path parameters
            if (suspiciousPath.matcher(fullUrl).find()) {
                reasons.add("Suspicious path/login patterns");
                score += 10;
            }
            
            // Check 7: Popular brand + typo (simple)
            String[] brands = {"paypal", "google", "amazon", "microsoft", "netflix"};
            for (String brand : brands) {
                if (host.contains(brand) && (host.contains("1") || host.contains("l") || host.contains("0"))) {
                    reasons.add("Typo-squatting brand: " + brand);
                    score += 25;
                    break;
                }
            }
            
            // Cap score & default safe
            score = Math.min(100, score);
            if (score == 0) {
                score = 5;
                reasons.add("No phishing indicators found");
            }
            
            result.setRiskScore(score);
            result.setReasons(reasons);
            result.setVerdict(getVerdict(score));
            
            return result;
            
        } catch (Exception e) {
            PhishingResult errorResult = new PhishingResult();
            errorResult.setUrl(urlStr);
            errorResult.setRiskScore(50);
            errorResult.setVerdict("ERROR");
            errorResult.addReason("Invalid URL: " + e.getMessage());
            return errorResult;
        }
    }
    
    private String getVerdict(int score) {
        if (score >= 80) return "PHISH";
        if (score >= 40) return "RISKY";
        return "SAFE";
    }
    
    public static class PhishingResult {
        private String url;
        private int riskScore;
        private String verdict;
        private List<String> reasons = new ArrayList<>();
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public int getRiskScore() { return riskScore; }
        public void setRiskScore(int riskScore) { this.riskScore = riskScore; }
        public String getVerdict() { return verdict; }
        public void setVerdict(String verdict) { this.verdict = verdict; }
        public List<String> getReasons() { return reasons; }
        public void setReasons(List<String> reasons) { this.reasons = reasons; }
        public void addReason(String reason) { this.reasons.add(reason); }
    }
}

