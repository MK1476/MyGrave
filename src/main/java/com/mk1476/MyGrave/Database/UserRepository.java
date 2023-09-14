package com.mk1476.MyGrave.Database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findBySpaceName(String spaceName);
}
