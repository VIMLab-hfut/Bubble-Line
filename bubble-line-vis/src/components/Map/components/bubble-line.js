import {OutlineGenerator} from "./outlineGenerator.js";
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
        console.log(anchorPoints)
        let outline = new PathGenerator(anchorPoints).transform([
            new ShapeSimplifier(),
            new BSplineShapeGenerator(),
            new ShapeSimplifier(),
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
        // let o1 = new PathGenerator(anchorPoints);
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

function BSplineShapeGenerator() {
    var ORDER = 3;
    var START_INDEX = ORDER - 1;
    var REL_END = 1;
    var REL_START = REL_END - ORDER;

    var that = this;
    var clockwise = true;
    var granularity = 6.0;
    this.granularity = function (_) {
        if (!arguments.length) return granularity;
        granularity = _;
    };

    function basicFunction(i, t) {
        // the basis function for a cubic B spline
        switch (i) {
            case -2:
                return (((-t + 3.0) * t - 3.0) * t + 1.0) / 6.0;
            case -1:
                return (((3.0 * t - 6.0) * t) * t + 4.0) / 6.0;
            case 0:
                return (((-3.0 * t + 3.0) * t + 3.0) * t + 1.0) / 6.0;
            case 1:
                return (t * t * t) / 6.0;
            default:
                return;
        }
    }

    function getRelativeIndex(index, relIndex) {
        return index + (clockwise ? relIndex : -relIndex);
    }

    // evaluates a point on the B spline
    function calcPoint(path, i, t) {
        var px = 0.0;
        var py = 0.0;
        for (var j = REL_START; j <= REL_END; j += 1) {
            var p = path.get(getRelativeIndex(i, j));
            var bf = basicFunction(j, t);
            px += bf * p[0];
            py += bf * p[1];
        }
        return [px, py];
    }

    this.apply = function (path) {
        // covering special cases
        if (path.size() < 3) return path;
        // actual b-spline calculation
        var res = new PathGenerator();
        var count = path.size() + ORDER - 1;
        var g = that.granularity();
        var closed = path.closed();
        res.add(calcPoint(path, START_INDEX - (closed ? 0 : 2), 0));
        for (var ix = START_INDEX - (closed ? 0 : 2); ix < count + (closed ? 0 : 2); ix += 1) {
            for (var k = 1; k <= g; k += 1) {
                res.add(calcPoint(path, ix, k / g));
            }
        }
        return res;
    };
}

function ShapeSimplifier() {
    var that = this;
    var tolerance = 0.0;
    var tsqr = 0.0;
    this.tolerance = function (_) {
        if (!arguments.length) return tolerance;
        tolerance = _;
        tsqr = tolerance * tolerance;
    };
    this.isDisabled = function () {
        return tolerance < 0.0;
    };

    function State(path, _start) {
        var sthat = this;
        var start = _start;
        var end = _start + 1;

        this.advanceEnd = function () {
            end += 1;
        };
        this.decreaseEnd = function () {
            end -= 1;
        };
        this.end = function () {
            return end;
        };
        this.validEnd = function () {
            return path.closed() ? end < path.size() : end < path.size() - 1;
        };
        this.endPoint = function () {
            return path.get(end);
        };
        this.startPoint = function () {
            return path.get(start);
        };
        this.lineDstSqr = function (ix) {
            var p = path.get(ix);
            var s = sthat.startPoint();
            var e = sthat.endPoint();
            return OutlineGenerator.linePtSegDistSq(s[0], s[1], e[0], e[1], p[0], p[1]);
        };
        this.canTakeNext = function () {
            if (!sthat.validEnd()) return false;
            var ok = true;
            sthat.advanceEnd();
            for (var ix = start + 1; ix < end; ix += 1) {
                if (sthat.lineDstSqr(ix) > tsqr) {
                    ok = false;
                    break;
                }
            }
            sthat.decreaseEnd();
            return ok;
        };
    } // State

    this.apply = function (path) {
        if (that.isDisabled() || path.size() < 3) return path;
        var states = [];
        var start = 0;
        while (start < path.size()) {
            var s = new State(path, start);
            while (s.canTakeNext()) {
                s.advanceEnd();
            }
            start = s.end();
            states.push(s);
        }
        return new PathGenerator(states.map(function (s) {
            return s.startPoint();
        }));
    };

    if (arguments.length) {
        that.tolerance(_tolerance);
    }
}

function PathGenerator(_points) {
    var that = this;
    var arr = [];
    var closed = true;
    this.closed = function (_) {
        if (!arguments.length) return closed;
        closed = _;
    };

    this.addAll = function (points) {
        points.forEach(function (p) {
            that.add(p);
        });
    };
    this.add = function (p) {
        var x = p[0];
        var y = p[1];
        if (Number.isNaN(x) || Number.isNaN(y)) {
        }
        arr.push([x, y]);
    };
    this.size = function () {
        return arr.length;
    };
    this.get = function (ix) {
        var size = that.size();
        var closed = that.closed();
        if (ix < 0) {
            return closed ? that.get(ix + size) : that.get(0);
        }
        if (ix >= size) {
            return closed ? that.get(ix - size) : that.get(size - 1);
        }
        return arr[ix];
    };
    this.forEach = function (cb) {
        arr.forEach(function (el, ix) {
            cb(el, ix, that);
        });
    };
    this.isEmpty = function () {
        return !that.size();
    };
    this.transform = function (ts) {
        var path = that;
        ts.forEach(function (t) {
            path = t.apply(path);
        });
        return path;
    };

    if (arguments.length && _points) {
        that.addAll(_points);
    }
}

PathGenerator.prototype.toString = function () {
    var that = this;
    var outline = "";
    that.forEach(function (p) {
        if (!outline.length) {
            outline += "M" + p[0] + " " + p[1];
        } else {
            outline += " L" + p[0] + " " + p[1];
        }
    });
    if (!outline.length) {
        return "M0 0";
    }
    if (that.closed()) {
        outline += " Z";
    }
    return outline;
};

export {BubbleLine};
