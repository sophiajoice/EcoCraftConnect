async function router(view) {
    const app = document.getElementById('app-view');
    const searchTerm = document.getElementById('searchInput').value;

    if (view === 'home') {
        app.innerHTML = `<div class="view-section"><h2>Top Picks</h2><div id="grid" class="product-grid"></div></div>`;
        renderProducts('');
    } else if (view === 'search') {
        app.innerHTML = `<div class="view-section"><h2>Results for "${searchTerm}"</h2><div id="grid" class="product-grid"></div></div>`;
        renderProducts(searchTerm);
    } else if (view === 'sell') {
        app.innerHTML = `
            <div class="view-section">
                <div class="sell-form">
                    <h2>Sell on EcoCraft</h2>
                    <input id="pName" placeholder="Product Name">
                    <select id="pCat"><option value="Scrap">Scrap</option><option value="First-Hand">First-Hand</option></select>
                    <input id="pPrice" type="number" placeholder="Price ($)">
                    <input id="pImg" placeholder="Image URL (e.g., media/item1.jpg)">
                    <button class="btn-primary" style="background:#febd69; padding:10px; border:none; font-weight:bold; cursor:pointer" onclick="handleSell()">List Product</button>
                </div>
            </div>`;
    }
}

async function renderProducts(term) {
    const res = await fetch(`/api/products?search=${term}`);
    const products = await res.json();
    const grid = document.getElementById('grid');
    grid.innerHTML = products.map(p => `
        <div class="product-card">
            <img src="${p.img || 'https://via.placeholder.com/200'}" alt="product">
            <h3>${p.name}</h3>
            <p style="color:#b12704; font-size:18px; font-weight:bold">$${p.price}</p>
            <p style="font-size:12px; color:#565959">Category: ${p.category}</p>
            <button style="width:100%; padding:8px; background:#ffd814; border:1px solid #fcd200; border-radius:20px; cursor:pointer">Add to Cart</button>
        </div>
    `).join('');
}

async function handleSell() {
    const data = {
        name: document.getElementById('pName').value,
        category: document.getElementById('pCat').value,
        price: document.getElementById('pPrice').value,
        img: document.getElementById('pImg').value
    };
    await fetch('/api/sell', { method: 'POST', body: JSON.stringify(data) });
    alert("Product Listed Successfully!");
    router('home');
}

window.onload = () => router('home');