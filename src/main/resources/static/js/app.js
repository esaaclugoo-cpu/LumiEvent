(function () {
    'use strict';

    // Normaliza texto para búsquedas (minúsculas, sin acentos y sin espacios sobrantes).
    function normalizar(texto) {
        return (texto || '')
            .toString()
            .toLowerCase()
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .trim();
    }

    // Convierte valores de dataset o inputs a número decimal seguro con fallback.
    function parseNumber(value, fallback) {
        if (value === null || value === undefined || value === '') {
            return fallback;
        }
        var parsed = parseFloat(String(value).replace(',', '.'));
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    // Inicializa filtros y buscador de la pantalla home cuando existe el grid de eventos.
    function initHome() {
        var grid = document.getElementById('eventos-grid');
        if (!grid) {
            return;
        }

        // Estado local de filtros para combinar tipo, disponibilidad y búsqueda de texto.
        var filtroTipo = 'all';
        var filtroDisp = 'all';
        var textoBusqueda = '';

        // Construye el texto indexable de una card a partir de título, ubicación y categoría.
        function textoCard(col) {
            var titulo = col.querySelector('.evento-card-title');
            var ubicacion = col.querySelector('.evento-location');
            var badgeTipo = col.querySelector('.tipo-badge');
            return normalizar(
                (titulo ? titulo.textContent : '') + ' ' +
                (ubicacion ? ubicacion.textContent : '') + ' ' +
                (badgeTipo ? badgeTipo.textContent : '')
            );
        }

        // Aplica todos los filtros activos sobre el grid y gestiona el mensaje "sin resultados".
        function aplicarFiltros() {
            var cards = document.querySelectorAll('#eventos-grid > div[data-tipo]');
            var visibles = 0;
            cards.forEach(function (col) {
                var tipo = normalizar(col.dataset.tipo);
                var disp = col.dataset.disp;
                var matchTipo = filtroTipo === 'all' || tipo === filtroTipo;
                var matchDisp = filtroDisp === 'all' || disp === filtroDisp;
                var matchTexto = textoBusqueda === '' || textoCard(col).indexOf(textoBusqueda) !== -1;

                if (matchTipo && matchDisp && matchTexto) {
                    col.style.display = '';
                    visibles++;
                } else {
                    col.style.display = 'none';
                }
            });

            var sinResultados = document.getElementById('sin-resultados');
            if (sinResultados) {
                sinResultados.classList.toggle('d-none', visibles > 0);
            }
        }

        // Escucha cambios del filtro por tipo de evento.
        document.querySelectorAll('input[name="filtro-tipo"]').forEach(function (r) {
            r.addEventListener('change', function () {
                filtroTipo = this.value;
                aplicarFiltros();
            });
        });

        // Escucha cambios del filtro por disponibilidad.
        document.querySelectorAll('input[name="filtro-disp"]').forEach(function (r) {
            r.addEventListener('change', function () {
                filtroDisp = this.value;
                aplicarFiltros();
            });
        });

        // Conecta el buscador del header para filtrar resultados en tiempo real.
        var inputBusqueda = document.getElementById('busqueda-eventos');
        if (inputBusqueda) {
            inputBusqueda.addEventListener('input', function () {
                textoBusqueda = normalizar(this.value);
                aplicarFiltros();
            });
        }

        // Sincroniza la flecha visual de cada bloque colapsable de filtros.
        document.querySelectorAll('.filter-toggle-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                this.classList.toggle('collapsed');
            });
        });
    }

    // Inicializa cálculo de total y disponibilidad dinámica en la ficha de un evento.
    function initHomeEvento() {
        var tipo = document.getElementById('tipo');
        var cantidad = document.getElementById('cantidad');
        var total = document.getElementById('totalEstimado');
        var maximoTipo = document.getElementById('maximoTipo');
        var boton = document.getElementById('btnAnadirCarrito');
        var sinDisponibilidad = document.getElementById('sinDisponibilidad');
        var form = document.getElementById('form-compra-evento');

        if (!tipo || !cantidad || !total || !maximoTipo || !boton || !sinDisponibilidad || !form) {
            return;
        }

        // Lee stock disponible por tipo desde atributos data-* del formulario.
        var disponiblesPorTipo = {
            general: parseInt(form.dataset.dispGeneral || '0', 10) || 0,
            vip: parseInt(form.dataset.dispVip || '0', 10) || 0,
            premium: parseInt(form.dataset.dispPremium || '0', 10) || 0
        };

        // Lee precios por tipo desde atributos data-* y los normaliza a número.
        var precios = {
            general: parseNumber(form.dataset.precioGeneral, 0),
            vip: parseNumber(form.dataset.precioVip, 0),
            premium: parseNumber(form.dataset.precioPremium, 0)
        };

        // Devuelve el stock disponible para el tipo actualmente seleccionado.
        function obtenerDisponiblesTipo() {
            var clave = (tipo.value || '').toLowerCase();
            return parseInt(disponiblesPorTipo[clave] || 0, 10) || 0;
        }

        // Ajusta máximo de cantidad, estado del botón y mensaje de falta de stock.
        function actualizarDisponibilidad() {
            var disponibles = obtenerDisponiblesTipo();
            maximoTipo.textContent = String(disponibles);
            cantidad.max = String(Math.max(disponibles, 1));

            if (disponibles <= 0) {
                boton.disabled = true;
                sinDisponibilidad.classList.remove('d-none');
            } else {
                boton.disabled = false;
                sinDisponibilidad.classList.add('d-none');
                if (parseInt(cantidad.value || '1', 10) > disponibles) {
                    cantidad.value = String(disponibles);
                }
            }
        }

        // Recalcula el total estimado (precio x cantidad) mostrado al usuario.
        function calcular() {
            var clave = (tipo.value || '').toLowerCase();
            var precio = precios[clave] || 0;
            var cant = parseInt(cantidad.value || '0', 10);
            total.textContent = (precio * Math.max(cant, 0)).toFixed(2);
        }

        // Reacciona a cambios de tipo para actualizar stock y total estimado.
        tipo.addEventListener('change', function () {
            actualizarDisponibilidad();
            calcular();
        });

        // Recalcula total cuando cambia la cantidad.
        cantidad.addEventListener('input', calcular);

        // Carga estado inicial al abrir la página.
        actualizarDisponibilidad();
        calcular();
    }

    // Muestra/oculta campos específicos del formulario de evento según su tipo.
    function initFormEvento() {
        var select = document.getElementById('tipoEvento');
        if (!select) {
            return;
        }

        // Controla la visibilidad de bloques .tipo-specific por tipo seleccionado.
        function actualizarCamposPorTipo(tipo) {
            document.querySelectorAll('.tipo-specific').forEach(function (div) {
                div.style.display = 'none';
            });
            if (tipo === 'concierto') {
                var concierto = document.getElementById('concierto-fields');
                if (concierto) concierto.style.display = 'block';
            } else if (tipo === 'teatro') {
                var teatro = document.getElementById('teatro-fields');
                if (teatro) teatro.style.display = 'block';
            } else if (tipo === 'museo') {
                var museo = document.getElementById('museo-fields');
                if (museo) museo.style.display = 'block';
            }
        }

        // Ejecuta una primera vez y deja suscrito el cambio del select.
        actualizarCamposPorTipo(select.value);
        select.addEventListener('change', function () {
            actualizarCamposPorTipo(this.value);
        });
    }

    // Activa el botón de mostrar/ocultar contraseña en la pantalla de login.
    function initLoginPasswordToggle() {
        var inputPassword = document.getElementById('password');
        var togglePassword = document.getElementById('togglePassword');
        var toggleIcon = document.getElementById('togglePasswordIcon');
        if (!inputPassword || !togglePassword) {
            return;
        }

        if (!toggleIcon) {
            toggleIcon = document.createElement('i');
            toggleIcon.id = 'togglePasswordIcon';
            toggleIcon.setAttribute('aria-hidden', 'true');
            togglePassword.innerHTML = '';
            togglePassword.appendChild(toggleIcon);
        }

        // Alterna tipo de input y actualiza icono y etiqueta accesible del botón.
        togglePassword.addEventListener('click', function () {
            var esPassword = inputPassword.type === 'password';
            inputPassword.type = esPassword ? 'text' : 'password';
            toggleIcon.classList.remove('bi-eye', 'bi-eye-slash');
            toggleIcon.classList.add('bi', esPassword ? 'bi-eye-slash' : 'bi-eye');
            togglePassword.setAttribute('aria-label', esPassword ? 'Ocultar contrasena' : 'Mostrar contrasena');
        });
    }

    // Punto de entrada: inicializa solo los módulos cuya vista está presente en el DOM.
    document.addEventListener('DOMContentLoaded', function () {
        initHome();
        initHomeEvento();
        initFormEvento();
        initLoginPasswordToggle();
    });
})();

