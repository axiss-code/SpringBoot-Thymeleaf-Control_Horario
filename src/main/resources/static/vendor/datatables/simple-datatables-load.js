window.addEventListener('DOMContentLoaded', event => {
	//https://github.com/fiduswriter/simple-datatables
    const datatablesSimple = document.getElementById('datatablesSimple');
	const datatablesTen = document.getElementById('datatablesTen');
	const datatablesUser = document.getElementById('datatablesUser');
	const datatablesArea = document.getElementById('datatablesArea');
	
    if (datatablesSimple) {
        new simpleDatatables.DataTable(datatablesSimple,{
			perPage: 5,
			columns: [
				{ select: 4, sort: "desc" }
			]
		});
    }
	
	if (datatablesTen) {
        new simpleDatatables.DataTable(datatablesTen,{
			perPage: 10,
			columns: [
				{ select: 0, sort: "asc" }
			]
		});
    }
	
	if (datatablesUser) {
        new simpleDatatables.DataTable(datatablesUser,{
			perPage: 10,
			columns: [
				{ select: 0, sort: "desc" }
			]
		});
    }
	
	if (datatablesArea) {
        new simpleDatatables.DataTable(datatablesArea,{
			perPage: 5,
			columns: [
				{ select: 0, sort: "asc" }
			]
		});
    }
});


