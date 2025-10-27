package com.example.proyecto_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyecto_app.data.Roles.RoleDao
import com.example.proyecto_app.data.Roles.RoleEntity
import com.example.proyecto_app.data.local.comentarios.CommentDao
import com.example.proyecto_app.data.local.comentarios.CommentEntity

import com.example.proyecto_app.data.local.publicacion.PublicacionDao
import com.example.proyecto_app.data.local.publicacion.PublicacionEntity
import com.example.proyecto_app.data.local.user.UserDao
import com.example.proyecto_app.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    //datos precargados y llamamos los entitis de cada uno
    entities = [UserEntity::class, PublicacionEntity::class, CommentEntity::class, RoleEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun UserDao(): UserDao
    abstract fun publicacionDao(): PublicacionDao
    abstract fun commentDao(): CommentDao

    abstract fun roleDao(): RoleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "pixelhub_app.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Usamos el Dispatcher.IO para operaciones de base de datos
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val roleDao = database.roleDao()
                                    val userDao = database.UserDao()
                                    val publicationDao = database.publicacionDao()

                                    // INSERTAR ROLES PRIMERO (SI NO EXISTEN)
                                    if (roleDao.count() == 0) {
                                        val initialRoles = listOf(
                                            RoleEntity(roleName = "admin"),    // Room asignará roleId=1
                                            RoleEntity(roleName = "usuario") // Room asignará roleId=2
                                        )
                                        roleDao.insertAll(initialRoles)
                                    }

                                    // OBTENER IDs DE ROLES
                                    //    Es más seguro obtenerlos por nombre.
                                    val adminRoleId = roleDao.getRoleByName("admin")?.roleId ?: 1L // Usamos 1L como fallback seguro
                                    val usuarioRoleId = roleDao.getRoleByName("usuario")?.roleId ?: 2L // Usamos 2L como fallback

                                    // INSERTAR USUARIOS CON roleId (SI NO EXISTEN)
                                    if (userDao.count() == 0) {
                                        val users = listOf(
                                            UserEntity(nameuser = "admin", email = "admin@gmail.com", phone = "12345678", pass = "Admin123!", roleId = adminRoleId),
                                            UserEntity(nameuser = "John Doe", email = "j@j.cl", phone = "87654321", pass = "Jose123!", roleId = usuarioRoleId)
                                        )
                                        users.forEach { userDao.insert(it) }

                                        // 4. INSERTAR PUBLICACIONES DE PRUEBA
                                        // Asegurarse que userId=2 exista (John Doe)
                                        // Necesitaríamos un count en PublicacionDao para ser estrictos
                                        val publications = listOf(
                                            PublicacionEntity(userId = 2, category = "Shooter", imageUri = null, title = "Bienvenido a pixelhub", authorName = "John Doe", likes = 42, description = "Primera publicación.")
                                        )
                                        publications.forEach { publicationDao.insert(it) }
                                    }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration() // Destruye y recrea si la versión cambia
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

