package com.neo.service;

import com.neo.domain.User;
import com.neo.dto.rq.UserDTOrq;
import com.neo.dto.rs.UserDTOrs;
import com.neo.exceptions.validation.AgeException;
import com.neo.exceptions.validation.UserNotFoundException;
import com.neo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Value("${age_limit}")
    private Integer ageLimit;

    public UserDTOrs createUser(UserDTOrq userDTOrq) {
        checkUserAge(userDTOrq.getBirthDate());
        User newUser = modelMapper.map(userDTOrq, User.class);
        newUser = userRepository.save(newUser);
        log.info("User with id: {} created!", newUser.getId());
        return modelMapper.map(newUser, UserDTOrs.class);
    }

    @Transactional
    @Modifying
    public UserDTOrs updateUser(Long userId, UserDTOrq userDTOrq) {
        checkUserAge(userDTOrq.getBirthDate());
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %s is not present")));
        userToUpdate = modelMapper.map(userDTOrq, User.class);
        userToUpdate.setId(userId);
        userRepository.save(userToUpdate);
        log.info("User with id: {} updated!", userId);
        return modelMapper.map(userToUpdate, UserDTOrs.class);
    }

    @Transactional
    @Modifying
    public UserDTOrs patchUser(Long userId, UserDTOrq userDTOrq) {
        User userToPatch = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %s is not present")));

        recursiveUpdateFields(userToPatch, userDTOrq);

        return modelMapper.map(userToPatch, UserDTOrs.class);
    }

    @Modifying
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id: %s is not present")));
        userRepository.deleteById(userId);
    }

    @Transactional(readOnly = true)
    public List<UserDTOrs> findUsersByBirthDateInRange(LocalDate from, LocalDate to) {
        //call DB by repository to get UserDTOrs and wrap them in ApiResponse
        return new ArrayList<>();
    }

    private void checkUserAge(LocalDate userBirthDate) {
        if (userBirthDate != null && userBirthDate.isAfter(LocalDate.now().minusYears(ageLimit)))
            throw new AgeException("You are too young my friend! This service is only for 18+ people");
    }

    @SneakyThrows
    private void recursiveUpdateFields(Object objectToPatch, Object sourceObject) {
        Field[] fields = sourceObject.getClass().getDeclaredFields();
        List<Object> objectsToDelete = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(sourceObject);
                if (value != null) {
                    Field objectToPatchField = objectToPatch.getClass().getDeclaredField(field.getName());
                    objectToPatchField.setAccessible(true);
                    if (objectToPatchField.isAnnotationPresent(Embedded.class)) {
                        recursiveUpdateFields(objectToPatchField.get(objectToPatch), value);
                    } else if (objectToPatchField.isAnnotationPresent(ElementCollection.class)) {
                        Collection<Object> sourceCollection = (Collection<Object>) value;
                        Collection<Object> collectionToPatch = (Collection<Object>) objectToPatchField.get(objectToPatch);

                        Iterator<?> sourceIterator = sourceCollection.iterator();
                        Iterator<?> patchedIterator = collectionToPatch.iterator();

                        if (sourceCollection.size() >= collectionToPatch.size()) {
                            while (sourceIterator.hasNext()) {
                                Object sourceElement = sourceIterator.next();
                                if (patchedIterator.hasNext()) {
                                    Object patchedElement = patchedIterator.next();
                                    recursiveUpdateFields(patchedElement, sourceElement);
                                } else {
                                    collectionToPatch.add(sourceElement);
                                }
                            }
                        } else {
                            while (patchedIterator.hasNext()) {
                                if (sourceIterator.hasNext()) {
                                    Object sourceElement = sourceIterator.next();
                                    Object patchedElement = patchedIterator.next();
                                    recursiveUpdateFields(patchedElement, sourceElement);
                                } else {
                                    objectsToDelete.add(patchedIterator.next());
                                }
                            }
                            for (Object objectToRemove : objectsToDelete) {
                                collectionToPatch.remove(objectToRemove);
                            }
                        }
                    } else {
                        objectToPatchField.set(objectToPatch, value);
                    }
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                log.info("Got exception in attempt to update field {}: {}", field.getName(), e.getMessage());
            }
        }
    }
}
