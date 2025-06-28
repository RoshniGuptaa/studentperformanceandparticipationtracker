/**
 * 
 */
function toggleSidebar() {
    const sidebar = document.querySelector(".sidebar");
    const content = document.querySelector(".content");

    // Toggle class to show/hide sidebar
    sidebar.classList.toggle("active");
    content.classList.toggle("expanded");
}

function toggleSidebar2() {

     document.body.classList.toggle("sidebar-collapsed")
}
