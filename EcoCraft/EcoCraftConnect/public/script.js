window.onload = loadHome;

function showPopup(message) {
    const popup = document.getElementById("popup");
    popup.innerText = message;
    popup.style.display = "block";
    setTimeout(() => popup.style.display = "none", 3000);
}

function loadHome() {
    fetch('/api/products')
        .then(res => res.json())
        .then(data => displayProducts(data));
}

function displayProducts(products) {
    const content = document.getElementById("content");
    content.innerHTML = "";

    products.forEach(p => {
        content.innerHTML += `
        <div class="card">
            <img src="${p.img}">
            <h3>${p.name}</h3>
            <div class="price">₹ ${p.price}</div>
            <div class="type">${p.type}</div>
            <button onclick='orderProduct(${JSON.stringify(p)})'>
                Add to Cart
            </button>
        </div>`;
    });
}

function searchGlobal() {
    const value = document.getElementById("globalSearch").value;
    fetch(`/api/products?search=${value}`)
        .then(res => res.json())
        .then(data => displayProducts(data));
}

function showSell() {
    const content = document.getElementById("content");
    content.innerHTML = `
    <div class="form-container">
        <h2>Sell Product</h2>
        <input id="name" placeholder="Product Name">
        <input id="category" placeholder="Category">
        <input id="price" placeholder="Price">
        <select id="type">
            <option>Scrapped</option>
            <option>First Hand</option>
        </select>
        <input id="img" placeholder="Image URL">
        <button onclick="sellProduct()">List Product</button>
    </div>`;
}

function sellProduct() {
    const product = {
        name: name.value,
        category: category.value,
        price: price.value,
        type: type.value,
        img: img.value
    };

    fetch('/api/sell', {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(product)
    }).then(() => {
        showPopup("Product Listed Successfully!");
        loadHome();
    });
}

function orderProduct(product) {
    fetch('/api/order', {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(product)
    }).then(() => showPopup("Order Placed Successfully!"));
}

function showOrders() {
    fetch('/api/orders')
        .then(res => res.json())
        .then(data => {
            const content = document.getElementById("content");
            content.innerHTML = "";

            data.forEach(o => {
                content.innerHTML += `
                <div class="card">
                    <h3>${o.product}</h3>
                    <div class="price">₹ ${o.price}</div>
                    <div class="type">Status: ${o.status}</div>
                </div>`;
            });
        });
}

function showLogin() {
    const content = document.getElementById("content");
    content.innerHTML = `
    <div class="form-container">
        <h2>Login</h2>
        <input id="username" placeholder="Username">
        <input id="password" type="password" placeholder="Password">
        <button onclick="login()">Login</button>
    </div>`;
}

function login() {
    fetch('/api/login', {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({
            username: username.value,
            password: password.value
        })
    }).then(res => {
        if(res.status === 200) showPopup("Login Successful!");
        else showPopup("Invalid Credentials");
    });
}