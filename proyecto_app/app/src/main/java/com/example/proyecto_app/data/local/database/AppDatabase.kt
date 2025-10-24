package com.example.proyecto_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
    entities = [UserEntity::class, PublicacionEntity::class, CommentEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase(){

    abstract fun UserDao(): UserDao
    abstract fun publicacionDao(): PublicacionDao

    abstract fun commentDao(): CommentDao


    companion object{
        @Volatile
        private var INSTANCE: AppDatabase?=null
        private const val BD_NAME="pixelhub_app.db"

        fun getInstance(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    BD_NAME
                )
                    .addCallback(object : RoomDatabase.Callback(){
                        override fun onCreate(db: SupportSQLiteDatabase){
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val userDao = database.UserDao()//llamamos las variables par hacer la inyeccion del dao
                                    val publicationDao = database.publicacionDao()

                                    val users = listOf(//creamos usuario
                                        UserEntity(nameuser = "admin", email = "admin@gmail.com", phone = "12345678", pass = "Admin123!"),
                                        UserEntity(nameuser = "John Doe", email = "j@j.cl", phone = "87654321", pass = "Jose123!")
                                    )

                                    if (userDao.count() == 0) {//si falla forsara a crear las cosas
                                        users.forEach { userDao.insert(it) }
                                    }

                                    // Esta parte  depende de los IDs que Room genere.
                                    val publications = listOf(
                                        PublicacionEntity(userId = 2, category = "Shooter", imageUri = null, title = "Bienvenido a pixelhub", authorName = "John Doe", likes = 42, description = "")
                                    )
                                    publications.forEach { publicationDao.insert(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

