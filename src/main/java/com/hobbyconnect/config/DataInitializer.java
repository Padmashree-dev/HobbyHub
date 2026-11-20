package com.hobbyconnect.config;

import com.hobbyconnect.model.Community;
import com.hobbyconnect.model.CommunityPost;
import com.hobbyconnect.model.Hobby;
import com.hobbyconnect.model.User;
import com.hobbyconnect.repository.CommunityPostRepository;
import com.hobbyconnect.repository.CommunityRepository;
import com.hobbyconnect.repository.HobbyRepository;
import com.hobbyconnect.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final HobbyRepository hobbyRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final CommunityPostRepository communityPostRepository;

    public DataInitializer(HobbyRepository hobbyRepository,
                           UserRepository userRepository,
                           CommunityRepository communityRepository,
                           CommunityPostRepository communityPostRepository) {
        this.hobbyRepository = hobbyRepository;
        this.userRepository = userRepository;
        this.communityRepository = communityRepository;
        this.communityPostRepository = communityPostRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (hobbyRepository.count() == 0) {
            initHobbies();
        }

        if (userRepository.count() == 0) {
            initSampleUsersAndCommunities();
        }
    }

    private void initHobbies() {
        List<Hobby> defaultHobbies = List.of(
            new Hobby("Music", "Creative Arts", "bi-music-note-beamed", "Singing, playing instruments, music production & discovering new songs"),
            new Hobby("Drawing", "Creative Arts", "bi-palette", "Sketching, digital art, acrylics, oil painting & creative illustration"),
            new Hobby("Coding", "Technology", "bi-code-slash", "Software building, web development, open-source projects & AI experiments"),
            new Hobby("Gaming", "Entertainment", "bi-controller", "PC, console, eSports, multiplayer raids & casual mobile games"),
            new Hobby("Photography", "Creative Arts", "bi-camera", "Landscape, portrait, street shots, lens gears & visual storytelling"),
            new Hobby("Dancing", "Performing Arts", "bi-activity", "Hip-hop, contemporary, salsa, classical & freestyle rhythm moves"),
            new Hobby("Sports", "Athletics", "bi-trophy", "Football, basketball, cricket, badminton, tennis & endurance track"),
            new Hobby("Cooking", "Culinary", "bi-egg-fried", "Baking delicious pastries, gourmet meals & international cuisines"),
            new Hobby("Reading", "Literature", "bi-book", "Novels, sci-fi, personal growth, book club reviews & creative writing"),
            new Hobby("Fitness", "Health", "bi-heart-pulse", "Weight training, yoga, calisthenics, running & holistic wellness"),
            new Hobby("Traveling", "Adventure", "bi-compass", "Backpacking, exploring nature spots, road trips & cultural adventures"),
            new Hobby("Gardening", "Nature", "bi-flower1", "Indoor plant care, balcony gardens, succulents & organic harvesting")
        );
        hobbyRepository.saveAll(defaultHobbies);
    }

    private void initSampleUsersAndCommunities() {
        Hobby music = hobbyRepository.findByName("Music").orElse(null);
        Hobby drawing = hobbyRepository.findByName("Drawing").orElse(null);
        Hobby coding = hobbyRepository.findByName("Coding").orElse(null);
        Hobby gaming = hobbyRepository.findByName("Gaming").orElse(null);
        Hobby photography = hobbyRepository.findByName("Photography").orElse(null);
        Hobby dancing = hobbyRepository.findByName("Dancing").orElse(null);
        Hobby sports = hobbyRepository.findByName("Sports").orElse(null);
        Hobby cooking = hobbyRepository.findByName("Cooking").orElse(null);
        Hobby fitness = hobbyRepository.findByName("Fitness").orElse(null);

        // 1. User: Alex Rivera (Coding, Gaming, Music)
        User alex = new User("Alex Rivera", "alexr", "alex@hobbyconnect.com", "password123",
                "Full-stack developer by day, indie synthwave musician by night! Always looking for co-op gaming buddies and coding partners.",
                "San Francisco, CA", "avatar-1");
        if (coding != null) alex.getHobbies().add(coding);
        if (gaming != null) alex.getHobbies().add(gaming);
        if (music != null) alex.getHobbies().add(music);
        userRepository.save(alex);

        // 2. User: Maya Chen (Drawing, Photography, Dancing)
        User maya = new User("Maya Chen", "mayac", "maya@hobbyconnect.com", "password123",
                "Digital concept artist & visual photographer. Love capturing urban life and attending weekend dance workshops!",
                "New York, NY", "avatar-2");
        if (drawing != null) maya.getHobbies().add(drawing);
        if (photography != null) maya.getHobbies().add(photography);
        if (dancing != null) maya.getHobbies().add(dancing);
        userRepository.save(maya);

        // 3. User: Liam Thorne (Sports, Fitness, Gaming)
        User liam = new User("Liam Thorne", "liamt", "liam@hobbyconnect.com", "password123",
                "Marathon runner, weekend footballer, and casual RPG gamer. Looking for running partners and workout buddies!",
                "Austin, TX", "avatar-3");
        if (sports != null) liam.getHobbies().add(sports);
        if (fitness != null) liam.getHobbies().add(fitness);
        if (gaming != null) liam.getHobbies().add(gaming);
        userRepository.save(liam);

        // 4. User: Sophia Patel (Music, Coding, Drawing)
        User sophia = new User("Sophia Patel", "sophiap", "sophia@hobbyconnect.com", "password123",
                "UI/UX enthusiast who loves coding interactive web experiences, acoustic guitar jams, and sketching coffee shop scenes.",
                "Seattle, WA", "avatar-4");
        if (music != null) sophia.getHobbies().add(music);
        if (coding != null) sophia.getHobbies().add(coding);
        if (drawing != null) sophia.getHobbies().add(drawing);
        userRepository.save(sophia);

        // 5. User: Daniel Kim (Photography, Traveling, Cooking)
        User daniel = new User("Daniel Kim", "danielk", "daniel@hobbyconnect.com", "password123",
                "Foodie traveler roaming with a Sony A7 IV. Always testing new ramen recipes and searching for scenic viewpoints.",
                "Chicago, IL", "avatar-5");
        if (photography != null) daniel.getHobbies().add(photography);
        if (cooking != null) daniel.getHobbies().add(cooking);
        if (music != null) daniel.getHobbies().add(music);
        userRepository.save(daniel);

        // 6. User: Chloe Bennett (Dancing, Music, Fitness)
        User chloe = new User("Chloe Bennett", "chloeb", "chloe@hobbyconnect.com", "password123",
                "Dance instructor & choreography enthusiast! Passionate about contemporary dance, R&B playlists, and pilates mornings.",
                "Los Angeles, CA", "avatar-6");
        if (dancing != null) chloe.getHobbies().add(dancing);
        if (music != null) chloe.getHobbies().add(music);
        if (fitness != null) chloe.getHobbies().add(fitness);
        userRepository.save(chloe);

        // Create initial vibrant communities
        Community c1 = new Community("Code Crafters Guild",
                "A lively collective for programmers, hackers, and creators building apps, sharing cool tools, and learning together.",
                "Technology", "bi-code-slash", alex);
        c1.getMembers().add(alex);
        c1.getMembers().add(sophia);
        communityRepository.save(c1);

        Community c2 = new Community("Melody Makers Collective",
                "For musicians, vocalists, producers, and lyricists! Share riffs, collaborate on tracks, and discuss favorite albums.",
                "Creative Arts", "bi-music-note-beamed", alex);
        c2.getMembers().add(alex);
        c2.getMembers().add(sophia);
        c2.getMembers().add(chloe);
        communityRepository.save(c2);

        Community c3 = new Community("Shutterbug Explorers",
                "Photography enthusiasts trading tips on composition, golden hour spots, camera lenses, and post-processing presets.",
                "Creative Arts", "bi-camera", daniel);
        c3.getMembers().add(daniel);
        c3.getMembers().add(maya);
        communityRepository.save(c3);

        Community c4 = new Community("Apex Gamers Lounge",
                "From chill co-op games to intense multiplayer lobbies. Find squadmates, share clips, and setup gaming nights!",
                "Entertainment", "bi-controller", liam);
        c4.getMembers().add(liam);
        c4.getMembers().add(alex);
        communityRepository.save(c4);

        Community c5 = new Community("Rhythm & Beats Dance Tribe",
                "Connect with dancers of all styles! Share dance reels, choreo tutorials, workshop schedules, and positive vibes.",
                "Performing Arts", "bi-activity", chloe);
        c5.getMembers().add(chloe);
        c5.getMembers().add(maya);
        communityRepository.save(c5);

        // Seed initial posts in communities
        communityPostRepository.save(new CommunityPost(
                "Welcome to Code Crafters! What programming languages or personal side-projects are you all currently working on? 💻✨",
                c1, alex));
        communityPostRepository.save(new CommunityPost(
                "Currently building a Spring Boot + Bootstrap mini project for hobbyists! Loving how clean the architecture looks. 🔥",
                c1, sophia));

        communityPostRepository.save(new CommunityPost(
                "Hey everyone! Dropping a question: what's your go-to acoustic guitar track to warm up with? 🎸",
                c2, sophia));

        communityPostRepository.save(new CommunityPost(
                "Caught an amazing sunrise fog at the coast this morning! Remember to shoot in RAW to preserve those highlight details. 📷🌅",
                c3, daniel));

        communityPostRepository.save(new CommunityPost(
                "Anyone down for a Saturday evening co-op session? Drop your tags below! 🎮👾",
                c4, liam));
    }
}
