package org.iesvdm.controlador;

import org.iesvdm.modelo.Pedido;
import org.iesvdm.servicios.ClienteService;
import org.iesvdm.servicios.ComercialService;
import org.iesvdm.servicios.PedidoService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PedidoController {
    private final PedidoService pedidoService;
    private final ClienteService clienteService;
    private final ComercialService comercialService;

    public PedidoController(PedidoService pedidoService, ClienteService clienteService, ComercialService comercialService) {
        this.pedidoService = pedidoService;
        this.clienteService = clienteService;
        this.comercialService = comercialService;
    }

    @GetMapping("/pedidos")
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listAllDetalles());
        return "pedidos";
    }

    @GetMapping("/pedidos/{id}")
    public String detalle(@PathVariable int id, Model model) {
        return pedidoService.findDetalle(id).map(pedido -> {
            model.addAttribute("pedido", pedido);
            return "detalle-pedido";
        }).orElse("redirect:/pedidos");
    }

    @GetMapping("/pedidos/crear")
    public String crear(Model model) {
        model.addAttribute("pedido", new Pedido());
        cargarOpciones(model);
        return "crear-pedido";
    }

    @PostMapping("/pedidos/crear")
    public String submitCrear(@ModelAttribute("pedido") Pedido pedido, Model model, RedirectAttributes redirectAttributes) {
        String error = validar(pedido);
        if (error != null) {
            model.addAttribute("error", error);
            cargarOpciones(model);
            return "crear-pedido";
        }
        try {
            pedidoService.create(pedido);
            redirectAttributes.addFlashAttribute("mensaje", "Pedido creado correctamente.");
            return "redirect:/pedidos";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("error", "No se ha podido guardar el pedido. Comprueba que el cliente y el comercial siguen existiendo.");
            cargarOpciones(model);
            return "crear-pedido";
        }
    }

    @GetMapping("/pedidos/editar/{id}")
    public String editar(@PathVariable int id, Model model) {
        return pedidoService.find(id).map(pedido -> {
            model.addAttribute("pedido", pedido);
            cargarOpciones(model);
            return "editar-pedido";
        }).orElse("redirect:/pedidos");
    }

    @PostMapping("/pedidos/editar/{id}")
    public String submitEditar(@PathVariable int id, @ModelAttribute("pedido") Pedido pedido, Model model, RedirectAttributes redirectAttributes) {
        if (pedidoService.find(id).isEmpty()) return "redirect:/pedidos";
        pedido.setId(id);
        String error = validar(pedido);
        if (error != null) {
            model.addAttribute("error", error);
            cargarOpciones(model);
            return "editar-pedido";
        }
        try {
            pedidoService.update(pedido);
            redirectAttributes.addFlashAttribute("mensaje", "Pedido actualizado correctamente.");
            return "redirect:/pedidos";
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("error", "No se ha podido actualizar el pedido. Comprueba que el cliente y el comercial siguen existiendo.");
            cargarOpciones(model);
            return "editar-pedido";
        }
    }

    @PostMapping("/pedidos/borrar/{id}")
    public String borrar(@PathVariable int id, RedirectAttributes redirectAttributes) {
        pedidoService.delete(id);
        redirectAttributes.addFlashAttribute("mensaje", "Pedido eliminado correctamente.");
        return "redirect:/pedidos";
    }

    private void cargarOpciones(Model model) {
        model.addAttribute("clientes", clienteService.listAll());
        model.addAttribute("comerciales", comercialService.listAll());
    }

    private String validar(Pedido pedido) {
        if (pedido.getTotal() == null || !Double.isFinite(pedido.getTotal()) || pedido.getTotal() < 0) {
            return "El total debe ser un número válido igual o superior a cero.";
        }
        if (clienteService.find(pedido.getClienteId()).isEmpty()) return "Selecciona un cliente válido.";
        if (comercialService.find(pedido.getComercialId()).isEmpty()) return "Selecciona un comercial válido.";
        return null;
    }
}
