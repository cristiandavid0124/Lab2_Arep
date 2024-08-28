function Hello() {
    const name = document.getElementById('name-input-field').value;
    fetch(`/App/hello?name=${encodeURIComponent(name)}`)
        .then(response => response.text())
        .then(data => {
            document.getElementById('hello-message').textContent = data;
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('hello-message').textContent = 'Error al obtener el saludo';
        });
}

function getPi() {
    fetch('/App/pi')
        .then(response => response.text())
        .then(data => {
            document.getElementById('pi-value').textContent = `El valor de Pi es: ${data}`;
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('pi-value').textContent = 'Error al obtener el valor de Pi';
        });
}
