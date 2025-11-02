🌟 MyHobbiesApp 

Aplicación Android para gestionar hobbies, explorar perfiles de otros usuarios y editar mi perfil.

✨ Funcionalidades
- Validación de correo y dominio permitido (`@mh.pe`, `@gmail.com`, `@hotmail.com`)
- Registro con aceptación de Términos & Condiciones
- Explora: lista de personas y perfiles
- Perfil: datos personales, hobbies, galería, cerrar sesión
- Galería con imágenes de hobbies en el perfil
- Hobbies: agregar, listar y eliminar

🧭 Flujo principal
Acceso → valida correo/clave → guarda sesión  
Inicio → saludo + botón (Tour interactivo)  
Explora → lista de perfiles nuevos en la app 
Solicitudes →  aceptar o denegar
Chat → lista de amigos con quien chatear
Perfil → ver/editar datos y hobbies, cerrar sesión

🗺️ Roadmap
- Subida de 3 fotos a galería (Perfil)
- Notificaciones para llamar la ATENCIÓN al USUARIO con Worker (WorkManager)
- Autenticación y base de datos (Firebase)

🛠️ Stack
- Persistencia: SQLite (galería) y Firestore
- Autenticación: validación por dominio permitido
- Notificaciones / Jobs: WorkManager
- Firebase Auth y Firebase Database.
