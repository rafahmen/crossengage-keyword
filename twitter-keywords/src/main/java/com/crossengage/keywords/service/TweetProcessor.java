package com.crossengage.keywords.service;

import org.springframework.social.twitter.api.MentionEntity;
import org.springframework.social.twitter.api.Tweet;

import com.crossengage.keywords.domain.Profile;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author Rafael
*/
public class TweetProcessor implements Runnable {
    private static final Pattern HASHTAG_PATTERN = Pattern.compile("#\\w+");

    private GraphService graphService;
    private final BlockingQueue<Tweet> queue;

    public TweetProcessor(GraphService graphService, BlockingQueue<Tweet> queue) {
        this.graphService = graphService;
        this.queue = queue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Tweet tweet = queue.take();
                processTweet(tweet);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void processTweet(Tweet tweetEntity) {
        String lang = tweetEntity.getLanguageCode();
        String text = tweetEntity.getText();
        // filter non-English tweets:
        if (!"en".equals(lang)) {
            return;
        }

        Set<String> hashtags = hashtagsFromTweet(text);

        // filter tweets without hashtags:
        if (hashtags.isEmpty()) {
            return;
        }

        com.crossengage.keywords.domain.Tweet tweet = new com.crossengage.keywords.domain.Tweet(tweetEntity.getText(), tweetEntity.getCreatedAt(), tweetEntity.getLanguageCode());
        // Neo4J will automatically find existing record based on screenname due to unique index on field:
        Profile author = graphService.createProfile(tweetEntity.getFromUser());

        tweet = graphService.createTweet(tweet);
        graphService.connectTweetWithAuthor(tweet, author);
        int mentions = connectTweetWithMentions(tweetEntity, tweet);

        String[] words = connectTweetWithTags(tweet, hashtags);
        System.out.printf("%d - %d - %s%n", mentions, words.length, text);
    }

    private String[] connectTweetWithTags(com.crossengage.keywords.domain.Tweet tweet, Set<String> hashtags) {
        String[] words = hashtags.toArray(new String[hashtags.size()]);
        for (String word : words) {
            graphService.connectTweetWithTag(tweet, word);
        }
        return words;
    }

    private int connectTweetWithMentions(Tweet tweetEntity, com.crossengage.keywords.domain.Tweet tweet) {
        int mentions = 0;
        if (tweetEntity.getEntities() != null) {
            mentions = tweetEntity.getEntities().getMentions().size();
            for (MentionEntity mentionEntity : tweetEntity.getEntities().getMentions()) {
                Profile profile = graphService.createProfile(mentionEntity.getScreenName());

                graphService.connectTweetWithMention(tweet, profile);
            }
        }
        return mentions;
    }

    private static Set<String> hashtagsFromTweet(String text) {
        Set<String> hashtags = new HashSet<>();
        Matcher matcher = HASHTAG_PATTERN.matcher(text);
        while (matcher.find()) {
            String handle = matcher.group();
            // removing '#' prefix
            hashtags.add(handle.substring(1));
        }
        return hashtags;
    }
}
