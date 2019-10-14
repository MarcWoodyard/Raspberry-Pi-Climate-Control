// inspired by this shot: https://dribbble.com/shots/2783874-Exploring-Smart-Thermostat-Controls/attachments/568702


// utilities to create SVG
function polarToCartesian(centerX, centerY, radius, angleInDegrees) {
  var angleInRadians = (angleInDegrees+135) * Math.PI / 180.0;

  return {
    x: centerX + (radius * Math.cos(angleInRadians)),
    y: centerY + (radius * Math.sin(angleInRadians))
  };
}
function describeArc(x, y, radius, startAngle, endAngle, close){

    var start = polarToCartesian(x, y, radius, endAngle);
    var end = polarToCartesian(x, y, radius, startAngle);

    var arcSweep = endAngle - startAngle <= 180 ? "0" : "1";
    var d;
    if (close) {
      d = [
          "M", start.x, start.y, // moveto
          "A", radius, radius, 0, arcSweep, 0, end.x, end.y, // arcto
          "Z"
      ].join(" ");
    } else {
      d = [
          "M", start.x+15, start.y+15, // moveto
          "L", start.x, start.y,
          "A", radius, radius, 0, arcSweep, 0, end.x, end.y, // arcto
          "L", x-120,y+120
      ].join(" ");
    }

    return d;       
}
document.getElementById("arc1").setAttribute("d", describeArc(225, 175, 150, 0, 359.9, true));
document.getElementById("arc2").setAttribute("d", describeArc(225, 175, 150, 0, 270, false));