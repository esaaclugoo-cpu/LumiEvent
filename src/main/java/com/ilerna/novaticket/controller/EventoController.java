package com.ilerna.novaticket.controller;


import com.ilerna.novaticket.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EventoController {

    private final EventoService eventoService;

    @Autowired
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }




    // Listar todos los eventos para "/" y "/eventos"
    @GetMapping({"/", "/eventos"})
    public String listarEvento(Model model) {
        model.addAttribute("eventos", eventoService.listarTodosLosEventos());
        return "crudtest";
    }



//
//    // Mostrar formulario para agregar un nuevo alumno
//    @GetMapping("/alumnos/nuevo")
//    public String mostrarFormulario(Model model) {
//        model.addAttribute("alumno", new Alumno());
//        return "form";
//    }
//
//    @Value("${app.upload.dir}")
//    private String uploadDir;
//
//    @PostMapping("/alumnos/guardar")
//    public String guardarAlumno(@ModelAttribute Alumno alumno,
//                                @RequestParam("archivo") MultipartFile archivo) throws IOException {
//
//        if (archivo != null && !archivo.isEmpty()) {
//            String nombre = System.currentTimeMillis() + "_" + archivo.getOriginalFilename();
//
//            Path dir = Paths.get(uploadDir);
//            Files.createDirectories(dir);
//
//            Files.copy(archivo.getInputStream(), dir.resolve(nombre),
//                    StandardCopyOption.REPLACE_EXISTING);
//
//            alumno.setRutaArchivo(nombre);
//        }
//
//        alumnoService.guardarAlumno(alumno);
//        return "redirect:/alumnos";
//    }
//
//
//
//
//
//    // Mostrar formulario para editar un alumno existente
//    @GetMapping("/alumnos/editar/{id}")
//    public String mostrarFormularioEditar(@PathVariable int id, Model model) {
//        Alumno alumno = alumnoService.obtenerAlumnoPorId(id);
//        model.addAttribute("alumno", alumno);
//        return "form";
//    }
//
//    // Actualizar un alumno existente
//    @PostMapping("/alumnos/actualizar")
//    public String actualizarAlumno(@ModelAttribute Alumno alumno) {
//        alumnoService.actualizarAlumno(alumno);
//        return "redirect:/alumnos";
//    }
//
//    // Eliminar un alumno
//    @GetMapping("/alumnos/eliminar/{id}")
//    public String eliminarAlumno(@PathVariable int id) {
//        alumnoService.eliminarAlumno(id);
//        return "redirect:/alumnos";
//    }
}
