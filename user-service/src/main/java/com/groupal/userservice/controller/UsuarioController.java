package com.groupal.userservice.controller;

import com.groupal.userservice.entity.Usuario;
import com.groupal.userservice.model.Carro;
import com.groupal.userservice.model.Moto;
import com.groupal.userservice.service.UsuarioService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.getAll();
        if (usuarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable("id") int id) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<Usuario> guardarUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @CircuitBreaker(name = "carrosCB",fallbackMethod = "fallBackGetCarros")
    @GetMapping("/carros/{usuarioId}")
    public ResponseEntity<List<Carro>> getCarros(@PathVariable("usuarioId") int id) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        List<Carro> carros = usuarioService.getCarros(id);
        return ResponseEntity.ok(carros);
    }

    @CircuitBreaker(name = "motosCB", fallbackMethod = "fallBackGetMotos")
    @GetMapping("/motos/{usuarioId}")
    public ResponseEntity<List<Moto>> listarMotos(@PathVariable("usuarioId") int id) {
        Usuario usuario = usuarioService.getUsuarioById(id);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        List<Moto> motos = usuarioService.getMotos(id);
        return ResponseEntity.ok(motos);
    }

    @CircuitBreaker(name = "carrosCB", fallbackMethod = "fallBackSaveCarro")
    @PostMapping("/carro/{usuarioId}")
    public ResponseEntity<Carro> saveCarro(@PathVariable("usuarioId") int usuarioId, @RequestBody Carro carro) {
        Carro nuevoCarro = usuarioService.saveCarro(usuarioId, carro);
        return ResponseEntity.ok(nuevoCarro);
    }

    @CircuitBreaker(name = "motosCB", fallbackMethod = "fallBackSaveMoto")
    @PostMapping("/moto/{usuarioId}")
    public ResponseEntity<Moto> guardarMoto(@PathVariable("usuarioId") int usuarioId, @RequestBody Moto moto) {
        Moto nuevaMoto = usuarioService.saveMoto(usuarioId, moto);
        return ResponseEntity.ok(nuevaMoto);
    }

    @CircuitBreaker(name = "todosCB", fallbackMethod = "fallBackGetTodos")
    @GetMapping("/todos/{usuarioId}")
    public ResponseEntity<Map<String, Object>> listarTodosLosVehiculos(@PathVariable("usuarioId") int usuarioId) {
        Map<String, Object> resultado = usuarioService.getUsuarioAndVehiculos(usuarioId);
        return ResponseEntity.ok(resultado);
    }

    private ResponseEntity<List<Carro>> fallBackGetCarros(@PathVariable("usuarioId") int id,
                                                          RuntimeException excepcion) {
        return new ResponseEntity("El usuario : " + id + " tiene los carros en el taller", HttpStatus.OK);
    }

    private ResponseEntity<Carro> fallBackSaveCarro(@PathVariable("usuarioId") int id, @RequestBody Carro carro,
                                                    RuntimeException excepcion) {
        return new ResponseEntity("El usuario : " + id + " no tiene dinero para los carros", HttpStatus.OK);
    }

    private ResponseEntity<List<Moto>> fallBackGetMotos(@PathVariable("usuarioId") int id, RuntimeException excepcion) {
        return new ResponseEntity("El usuario : " + id + " tiene las motos en el taller", HttpStatus.OK);
    }

    private ResponseEntity<Moto> fallBackSaveMoto(@PathVariable("usuarioId") int id, @RequestBody Moto carro,
                                                  RuntimeException excepcion) {
        return new ResponseEntity("El usuario : " + id + " no tiene dinero para las motos", HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> fallBackGetTodos(@PathVariable("usuarioId") int id,
                                                                 RuntimeException excepcion) {
        return new ResponseEntity("El usuario : " + id + " tiene los vehiculos en el taller", HttpStatus.OK);
    }
}
