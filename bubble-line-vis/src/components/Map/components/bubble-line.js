import {BSplineShapeGenerator, OutlineGenerator, PointPath, ShapeSimplifier} from "./outlineGenerator.js";
import * as d3 from 'd3';

class BubbleLine {
    constructor(width, height) {
        this.i = 0;
        this.colors = ["black", "yellow", "green", "red"];
        //Create a SVG canvas
        let svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
        svg.setAttribute('class', "river-contour");
        svg.setAttribute('xmlns', "http://www.w3.org/2000/svg");
        svg.setAttribute('width', width);
        svg.setAttribute('height', height);
        svg.setAttribute('viewBox', "0 0 " + width + " " + height);
        //svg.setAttribute('style',"background:rgba(135,206,235,0.3);");

        //Used to generate contours path
        this.pathGenerator = new OutlineGenerator();

        this.svgElement = svg;

        this.outlineGroup = this.appendSVG(svg, "g");

        this.linePoints = [];

        this.opacity = 0.5;
        this.setAttribute(this.outlineGroup, {
            stroke: "rgba(0,0,0,0.8)",
            fill: "#009ac0",
            //fill: "white",
            'stroke-width': 2,
            //'stroke-linecap': 'round'
        })
    }

    clearPoints() {
        this.linePoints.length = 0;
    }

    pushPoint(point) {
        this.linePoints.push(point);
    }

    draw(filledColor = "#009ac0") {
        let pointArr = [];
        for (let point of this.linePoints) {
            pointArr.push({
                x: point.x,
                y: point.y,
                width: 0,
                height: 0
            });
        }
        let anchorPoints = this.pathGenerator.createOutline(
            pointArr
        );

        let outline = new PointPath(anchorPoints).transform([
            new ShapeSimplifier(0.0),
            new BSplineShapeGenerator(),
            new ShapeSimplifier(0.0),
        ]);

        //generate a path dom
        let path = this.appendSVG(this.outlineGroup, "path");
        // outline is a path that can be used for the attribute d of a path element
        this.setAttribute(path, {
            d: outline,
            fill: filledColor,
            opacity: 0.3
        });

        //未插值前
        // let o1 = new PointPath(anchorPoints);
        // let path2 = this.appendSVG(this.outlineGroup, "path");
        // // outline is a path that can be used for the attribute d of a path element
        // this.setAttribute(path2, {
        //     d: o1.toString(),
        //     opacity: 0.7
        // });
        this.clearPoints();
    }

    /**
     * 使用d3绘制坐标轴
     */
    drawCoordinateAxis() {

        let svg = d3.select("svg");
        var xScale = d3.scaleLinear()
            .domain([0, 500])
            .range([0, 500]);

        var xAxis = d3.axisBottom()
            .scale(xScale)
            .tickSize(10);
        var yAxis = d3.axisRight(xScale).ticks(10);

        svg.append('g')
            .call(xAxis)
            .setAttribute("transform", "translate(0,0)")
            .selectAll("text")
            .setAttribute("font-size", "10px")
            .setAttribute("dx", "0px");

        svg.append('g')
            .call(yAxis).setAttribute("transform", "translate(0,0)").selectAll("text");
    }

    setAttribute(elem, attr) {
        // console.log(attr)
        for (let key in attr) {
            let value = attr[key];
            if (value === null) {
                elem.removeAttribute(key);
            } else {
                elem.setAttribute(key, value);
            }
        }
    }

    style(elem, style) {
        for (let key in style) {
            let value = style[key];
            if (value === null) {
                delete elem.style.removeProperty(key);
            } else {
                elem.style.setProperty(key, value);
            }
        }
    }

    appendSVG(parent, name) {
        return parent.appendChild(
            document.createElementNS("http://www.w3.org/2000/svg", name)
        );
    }

    removeAllChildren(parent) {
        while (parent.firstChild) {
            parent.removeChild(parent.firstChild);
        }
    }
}

export {BubbleLine};
