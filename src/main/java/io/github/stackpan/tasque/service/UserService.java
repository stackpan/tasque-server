package io.github.stackpan.tasque.service;

import io.github.stackpan.tasque.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    User getMe();

    User updateProfilePicture(User user, MultipartFile file);

}
