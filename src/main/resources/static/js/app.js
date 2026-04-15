(function () {
    'use strict';

    function normalizar(texto) {
        return (texto || '')
            .toString()
            .toLowerCase()
            .normalize('NFD')
            .replace(/[\u0300-\u036f]/g, '')
            .trim();
    }

    function parseNumber(value, fallback) {
        if (value === null || value === undefined || value === '') {
            return fallback;
        }
        var parsed = parseFloat(String(value).replace(',', '.'));
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function initHome() {
        var grid = document.getElementById('eventos-grid');
        if (!grid) {
            return;
        }

        var filtroTipo = 'all';
        var filtroDisp = 'all';
        var textoBusqueda = '';

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

        document.querySelectorAll('input[name="filtro-tipo"]').forEach(function (r) {
            r.addEventListener('change', function () {
                filtroTipo = this.value;
                aplicarFiltros();
            });
        });

        document.querySelectorAll('input[name="filtro-disp"]').forEach(function (r) {
            r.addEventListener('change', function () {
                filtroDisp = this.value;
                aplicarFiltros();
            });
        });

        var inputBusqueda = document.getElementById('busqueda-eventos');
        if (inputBusqueda) {
            inputBusqueda.addEventListener('input', function () {
                textoBusqueda = normalizar(this.value);
                aplicarFiltros();
            });
        }

        document.querySelectorAll('.filter-toggle-btn').forEach(function (btn) {
            btn.addEventListener('click', function () {
                this.classList.toggle('collapsed');
            });
        });
    }

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

        var disponiblesPorTipo = {
            general: parseInt(form.dataset.dispGeneral || '0', 10) || 0,
            vip: parseInt(form.dataset.dispVip || '0', 10) || 0,
            premium: parseInt(form.dataset.dispPremium || '0', 10) || 0
        };

        var precios = {
            general: parseNumber(form.dataset.precioGeneral, 0),
            vip: parseNumber(form.dataset.precioVip, 0),
            premium: parseNumber(form.dataset.precioPremium, 0)
        };

        function obtenerDisponiblesTipo() {
            var clave = (tipo.value || '').toLowerCase();
            return parseInt(disponiblesPorTipo[clave] || 0, 10) || 0;
        }

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

        function calcular() {
            var clave = (tipo.value || '').toLowerCase();
            var precio = precios[clave] || 0;
            var cant = parseInt(cantidad.value || '0', 10);
            total.textContent = (precio * Math.max(cant, 0)).toFixed(2);
        }

        tipo.addEventListener('change', function () {
            actualizarDisponibilidad();
            calcular();
        });
        cantidad.addEventListener('input', calcular);

        actualizarDisponibilidad();
        calcular();
    }

    function initFormEvento() {
        var select = document.getElementById('tipoEvento');
        if (!select) {
            return;
        }

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

        actualizarCamposPorTipo(select.value);
        select.addEventListener('change', function () {
            actualizarCamposPorTipo(this.value);
        });
    }

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

        togglePassword.addEventListener('click', function () {
            var esPassword = inputPassword.type === 'password';
            inputPassword.type = esPassword ? 'text' : 'password';
            toggleIcon.classList.remove('bi-eye', 'bi-eye-slash');
            toggleIcon.classList.add('bi', esPassword ? 'bi-eye-slash' : 'bi-eye');
            togglePassword.setAttribute('aria-label', esPassword ? 'Ocultar contrasena' : 'Mostrar contrasena');
        });
    }

    document.addEventListener('DOMContentLoaded', function () {
        initHome();
        initHomeEvento();
        initFormEvento();
        initLoginPasswordToggle();
    });
})();

