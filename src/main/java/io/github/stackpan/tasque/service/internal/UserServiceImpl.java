package io.github.stackpan.tasque.service.internal;

import io.github.stackpan.tasque.entity.User;
import io.github.stackpan.tasque.fs.FileStorage;
import io.github.stackpan.tasque.repository.UserRepository;
import io.github.stackpan.tasque.security.AuthToken;
import io.github.stackpan.tasque.service.UserService;
import io.github.stackpan.tasque.util.StringGenerators;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    private final AuthToken authToken;

    private final FileStorage fileStorage;

    @Override
    public UserDetails loadUserByUsername(String principal) throws UsernameNotFoundException {
        return userRepository.findByPrincipal(principal)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user with principal: %s".formatted(principal)));
    }

    @Override
    public User getMe() {
        return userRepository.findById(authToken.getCurrentSubject())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    @Override
    @Transactional
    public User updateProfilePicture(User user, MultipartFile file) {
        var filename = generateFilename(file);

        try {
            if (user.getProfilePicture() != null) {
                fileStorage.delete(user.getProfilePicture());
            }

            fileStorage.save(filename, file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        user.setProfilePicture(filename);

        return userRepository.save(user);
    }

    private String generateFilename(MultipartFile file) {
        return StringGenerators.generate(24) + "." + getFileExtension(file);
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) return null;

        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < originalFilename.length() - 1) {
            return originalFilename.substring(dotIndex + 1);
        } else {
            return "";
        }
    }
}
