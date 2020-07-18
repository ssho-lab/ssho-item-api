package webcrawler.shopping.swipe.model;

import lombok.Getter;

@Getter
public enum SlackTarget {
    CH_BOT("https://hooks.slack.com/services/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",
            "system-monitoring");

    private final String webHookUrl;
    private final String channel;

    SlackTarget(String webHookUrl, String channel) {
        this.webHookUrl = webHookUrl;
        this.channel = channel;
    }
}
