(function () {
    const botao = document.querySelector('[data-menu]');
    const barra = document.querySelector('.barra-lateral');

    if (botao && barra) {
        botao.addEventListener('click', function () {
            barra.classList.toggle('aberta');
        });
    }

    document.querySelectorAll('[data-confirmar]').forEach(function (formulario) {
        formulario.addEventListener('submit', function (evento) {
            const mensagem = formulario.getAttribute('data-confirmar') || 'Confirmar esta ação?';
            if (!window.confirm(mensagem)) {
                evento.preventDefault();
            }
        });
    });

    document.querySelectorAll('.alerta').forEach(function (alerta) {
        window.setTimeout(function () {
            alerta.style.transition = 'opacity .4s ease';
            alerta.style.opacity = '0';
            window.setTimeout(function () {
                alerta.remove();
            }, 400);
        }, 6000);
    });
})();
