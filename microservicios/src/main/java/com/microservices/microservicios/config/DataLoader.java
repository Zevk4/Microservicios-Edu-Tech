package com.microservices.microservicios.config;

import com.microservices.microservicios.model.Curso;
import com.microservices.microservicios.model.Evaluacion;
import com.microservices.microservicios.model.Rol;
import com.microservices.microservicios.model.Usuario;
import com.microservices.microservicios.repository.CursoRepository;
import com.microservices.microservicios.repository.EvaluacionRepository;
import com.microservices.microservicios.repository.RolRepository;
import com.microservices.microservicios.repository.UsuarioRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.List;
import java.util.Arrays;

@Configuration
@Profile("dev")
public class DataLoader {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final CursoRepository cursoRepository;
    private final EvaluacionRepository evaluacionRepository;

    public DataLoader(RolRepository rolRepository, UsuarioRepository usuarioRepository,
                      CursoRepository cursoRepository, EvaluacionRepository evaluacionRepository
                      ) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.cursoRepository = cursoRepository;
        this.evaluacionRepository = evaluacionRepository;
    }

    @Bean
    public CommandLineRunner initDatabase(Faker faker) {
        return _ -> {
            if (rolRepository.count() == 0) {
                System.out.println("Cargando datos de ejemplo CHILEEEEE");

                // Roles
                Rol adminRole = new Rol("ADMIN");
                Rol userRole = new Rol("ESTUDIANTE");
                Rol instructorRole = new Rol("INSTRUCTOR");
                rolRepository.save(adminRole);
                rolRepository.save(userRole);
                rolRepository.save(instructorRole);

                // Usuarios
                usuarioRepository.save(new Usuario("Gino Emiliano", "admin@example.com", "pq123", adminRole));
                usuarioRepository.save(new Usuario("Camilo Garses", "usuario.chile@example.com", "pintoo23", userRole));

                for (int i = 0; i < 5; i++) {
                    String username = faker.internet().username();
                    String email = faker.internet().emailAddress();
                    String password = faker.internet().password();
                    usuarioRepository.save(new Usuario(username, password, email, userRole));
                }

                // Lista de títulos de cursos en español (ya existente)
                List<String> titulosCursos = Arrays.asList(
                    "Introducción a la Programación con Java",
                    "Desarrollo Web con Spring Boot y React",
                    "Bases de Datos Relacionales (SQL)",
                    "Inteligencia Artificial para Principiantes",
                    "Marketing Digital Avanzado",
                    "Fundamentos de Ciberseguridad",
                    "Diseño de Interfaces de Usuario (UI/UX)",
                    "Gestión de Proyectos Ágiles (Scrum)",
                    "Análisis de Datos con Python",
                    "Blockchain y Criptomonedas",
                    "Metodologías Ágiles",
                    "DevOps e Integración Continua"
                );

                // Cursos
                for (int i = 0; i < 7; i++) { 
                    Curso curso = new Curso();
                    curso.setTitulo(faker.options().option(titulosCursos.toArray(new String[0])));
                    curso.setCategoria(faker.options().option("Programación", "Diseño Gráfico", "Marketing Digital", "Idiomas", "Finanzas", "Ciencia de Datos", "Redes", "Ciberseguridad"));
                    curso.setDescripcion(faker.lorem().paragraph(3)); 
                    curso.setInstructor(faker.name().fullName());
                    curso.setPrice(faker.number().randomDouble(2, 10, 200));
                    curso.setPopularidad(faker.number().randomDouble(1, 3, 5));
                    cursoRepository.save(curso);
                }

                // Listas para tipos, estados y nombres base de evaluación en español
                List<String> tiposEvaluacion = Arrays.asList("Examen", "Tarea", "Proyecto", "Cuestionario", "Presentación", "Prueba de Diagnóstico");
                List<String> estadosEvaluacion = Arrays.asList("Pendiente", "Activo", "Finalizado", "Calificado", "En Revisión");
                List<String> prefijosNombresEvaluacion = Arrays.asList("Evaluación", "Control", "Trabajo", "Examen", "Entrega", "Análisis");
                List<String> sufijosNombresEvaluacion = Arrays.asList("Parcial", "Final", "Diagnóstico", "Integrador", "Recuperativo", "Práctico");


                cursoRepository.findAll().forEach(curso -> {
                    for (int i = 0; i < faker.number().numberBetween(1, 3); i++) {
                        Evaluacion evaluacion = new Evaluacion();
                        
                        // Generar un nombre de evaluación más dinámico y en español
                        String nombreBaseCurso = curso.getTitulo().length() > 30 ? 
                                curso.getTitulo().substring(0, 30) + "..." : curso.getTitulo();
                        evaluacion.setNombre(
                            faker.options().option(prefijosNombresEvaluacion.toArray(new String[0])) +
                            " " + nombreBaseCurso + " " +
                            faker.options().option(sufijosNombresEvaluacion.toArray(new String[0]))
                        );

                        evaluacion.setDescripcion(faker.lorem().paragraph(2)); 
                        evaluacion.setTipo(faker.options().option(tiposEvaluacion.toArray(new String[0]))); 
                        evaluacion.setFecha_inicio(LocalDateTime.now().plusDays(faker.number().numberBetween(1, 15)));
                        evaluacion.setFecha_termino(evaluacion.getFecha_inicio().plusHours(faker.number().numberBetween(1, 8)));
                        evaluacion.setDuracion(faker.number().numberBetween(30, 240));
                        evaluacion.setCalificacionMaxima(faker.number().randomDouble(0, 10, 100));
                        evaluacion.setEstado(faker.options().option(estadosEvaluacion.toArray(new String[0]))); 
                        evaluacion.setCurso(curso);
                        evaluacionRepository.save(evaluacion);
                    }
                });

                System.out.println("Chilenos cargados.");
            } else {
                System.out.println("La base de datos ya contiene datos. Omitiendo carga de datos de ejemplo.");
            }
        };
    }

    @Bean
    public Faker faker() {
        return new Faker(new Locale("es", "CL")); // Locale para español de Chile
    }
}