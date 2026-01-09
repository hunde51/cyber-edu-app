// Minimal JS for UI transitions and enhancements
document.addEventListener('DOMContentLoaded', function () {
    // Add small fade-in for glass panels
    document.querySelectorAll('.glass').forEach(el => {
        el.style.opacity = 0;
        el.style.transform = 'translateY(8px)';
        setTimeout(() => {
            el.style.transition = 'opacity 400ms ease, transform 400ms ease';
            el.style.opacity = 1;
            el.style.transform = 'translateY(0)';
        }, 50);
    });
});
