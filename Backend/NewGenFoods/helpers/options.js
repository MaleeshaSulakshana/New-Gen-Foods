module.exports = {
    formate: 'A4',
    orientation: 'portrait',
    border: '2mm',
    header: {
        height: '60mm',
        contents: `
       <div>
            <h3 style=" color: black;font-size:20;font-weight:800;text-align:center;">NEW GEN FOOD RESTAURANT</h3>
            <h5 style=" color: black;font-size:18;font-weight:600;text-align:center;">Order Receipt</h5>
            <h6 style=" color: black;font-size:18;font-weight:600;text-align:center;">Tel - 077 061 4800</h6>
            <h6 style=" color: black;font-size:18;font-weight:600;text-align:center;">Email - newgenfoodrestaurant@gmail.com</h6>
       </div>
        `
    },
    footer: {
        height: '20mm',
        contents: {
            first: '1',
            2: 'Second page',
            default: `
            <footer style="text-align:center;">
            <span style="color: #444; text-align:center;">{{page}}</span>/<span>{{pages}}</span>
            </footer>`,
            last: 'Last Page'
        }
    }
}