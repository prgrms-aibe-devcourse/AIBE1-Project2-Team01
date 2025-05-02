package org.sunday.projectpop.project.model.service;

import org.sunday.projectpop.project.model.entity.UserAccount;

public interface UserAccountService {
    UserAccount getUserById(String userId);
}