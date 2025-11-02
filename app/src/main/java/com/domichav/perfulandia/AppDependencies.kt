package com.domichav.perfulandia

import com.domichav.perfulandia.repository.AvatarRepository

class AppDependencies(
    val userRepository: UserRepository,
    val sessionManager: SessionManager,
    val database: AppDatabase,
    val apiService: AuthApiService,
    val userDao: UserDao,
    val avatarRepository: AvatarRepository  // ✨ Nueva dependencia
)

private fun buildDependencies(application: Application): AppDependencies {
    // ... código existente ...

    // 6. Crear AvatarRepository para persistencia del avatar
    val avatarRepository = AvatarRepository(application)

    return AppDependencies(
        userRepository = userRepository,
        sessionManager = sessionManager,
        database = database,
        apiService = apiService,
        userDao = userDao,
        avatarRepository = avatarRepository  // ✨ Agregar aquí
    )
}

// EEEEEEEEEEEEEEEEEEEEEEEEEEE CI (?)  O_o
