package com.jy.community.util;

import com.jy.community.domain.post.Post;
import com.jy.community.domain.post.PostRepository;
import com.jy.community.domain.user.User;
import com.jy.community.domain.user.UserRepository;
import com.jy.community.type.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Data implements CommandLineRunner {

  private static final String[] SAMPLE_TITLES = {
      "Sample Title 1", "Sample Title 2", "Sample Title 3", "Sample Title 4", "Sample Title 5"
  };
  private static final String[] SAMPLE_CONTENTS = {
      "Sample content 1", "Sample content 2", "Sample content 3", "Sample content 4",
      "Sample content 5"
  };
  private static final String[] SAMPLE_TAGS = {
      "tag1", "tag2", "tag3", "tag4", "tag5"
  };

  private static final Random RANDOM = new Random();
  private static final int BATCH_SIZE = 1000;

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Transactional
  public void run(String... args) {
    if (postRepository.count() > 0) {
      System.out.println("데이터 생성 x");
      return;
    }

    createUser();

    User user = userRepository.findByUsername("testUser");

    for (int i = 0; i < 1000000; i++) {
      List<String> tags = new ArrayList<>();
      for (int j = 0; j < 3; j++) {
        tags.add(SAMPLE_TAGS[RANDOM.nextInt(SAMPLE_TAGS.length)]);
      }

      Post post = Post.builder()
          .title(SAMPLE_TITLES[RANDOM.nextInt(SAMPLE_TITLES.length)])
          .content(SAMPLE_CONTENTS[RANDOM.nextInt(SAMPLE_CONTENTS.length)])
          .tags(tags)
          .user(user)
          .build();

      entityManager.persist(post);

      if (i % BATCH_SIZE == 0 && i > 0) {
        entityManager.flush();
        entityManager.clear();
        System.out.println(i + "개의 데이터 삽입 완료");
      }
    }
    entityManager.flush();
    entityManager.clear();

    System.out.println("100만 개의 데이터 추가 완료");
  }

  private User createUser() {
    User user = User.builder()
        .username("testUser")
        .password(passwordEncoder.encode("testPassword"))
        .nickname("testNickname")
        .role(Role.USER)
        .build();
    return userRepository.save(user);
  }
}
