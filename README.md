MH – MyHobbiesApp 🎨⚽🎶

App Android (Kotlin) para descubrir y compartir hobbies. Proyecto del curso Desarrollo de Aplicaciones Móviles I (parciales T1 y T2).

✨ Funcionalidades
-Validación de correo y dominio permitido (@mh.pe, @gmail.com, @hotmail.com)
-Registro con selección de hobbies y aceptación de T&C
-Lista de personas ordenada por hobbies en común
-Muestra Perfil (datos del usuario, hobbies y Cerrar sesión)
-Gestión de hobbies
-Historial 

🧱 Arquitectura 
-Activities “host”: AccesoActivity, InicioActivity, RegistroActivity, HistorialActivity
-Fragments: InicioFragment, ExploraFragment, ChatsFragment, PerfilFragment
-Almacenamiento local UserStore / HobbiesStore

🧭 Flujo principal
Acceso → valida correo/clave → guarda sesión con UserStore.setLogged()
Inicio → saludo + botón a Explora
Explora → orden por coincidencia de hobbies
Perfil → ver datos, editar hobbies y cerrar sesión

🛠️ Requisitos
-Android Studio Jellyfish o superior
-minSdk 21, targetSdk 35
-Kotlin 1.9+, Material3
