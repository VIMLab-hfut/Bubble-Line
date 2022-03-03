function OutlineGenerator() {

    var outlineWidth = 50;
    var squarePixel = 5;

    function Point(ax, ay) {
        var x = +ax;
        var y = +ay;
        this.x = function (_) {
            if (!arguments.length) return x;
            x = _;
        };
        this.y = function (_) {
            if (!arguments.length) return y;
            y = _;
        };
        this.distanceSq = function (p) {
            return Point.ptsDistanceSq(x, y, p.x(), p.y());
        };
        this.get = function () {
            return [x, y];
        };
    } // Point

    Point.ptsDistanceSq = function (x1, y1, x2, y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    };
    Point.doublePointsEqual = function (x1, y1, x2, y2, delta) {
        return Point.ptsDistanceSq(x1, y1, x2, y2) < delta * delta;
    };

    function PointList(size) {
        var els = 0;
        var arr = [];
        arr.length = size; // pre-allocating
        var set = {};

        function hash(p) {
            return p.x() + "x" + p.y();
        }

        this.add = function (p) {
            set[hash(p)] = p;
            arr[els] = p;
            els += 1;
        };
        this.contains = function (p) {
            var test = set[hash(p)];
            if (!test) return false;
            return test.x() === p.x() && test.y() === p.y();
        };
        this.isFirst = function (p) {
            if (!els) return false;
            var test = arr[0];
            return test.x() === p.x() && test.y() === p.y();
        };
        this.list = function () {
            return arr.filter(function (p) {
                return p;
            }).map(function (p) {
                return p.get();
            });
        };
        this.clear = function () {
            for (var i = 0; i < arr.length; i += 1) {
                arr[i] = null; // nulling is cheaper than deleting or reallocating
            }
            set = {};
            els = 0;
        };
        this.get = function (ix) {
            return arr[ix];
        };
        this.size = function () {
            return els;
        };
    } // PointList

    function FilledElement(_rect) {
        var that = this;
        var x = 0;
        var y = 0;
        var width = 0;
        var height = 0;
        var centroidDistance = 0;

        this.rect = function (r) {
            if (!arguments.length) return {
                x: x,
                y: y,
                width: width,
                height: height
            };
            x = +r.x;
            y = +r.y;
            width = +r.width;
            height = +r.height;
        };
        this.minX = function () {
            return x;
        };
        this.minY = function () {
            return y;
        };
        this.maxX = function () {
            return x + width;
        };
        this.maxY = function () {
            return y + height;
        };
        this.centerX = function () {
            return x + width * 0.5;
        };
        this.centerY = function () {
            return y + height * 0.5;
        };
        this.width = function () {
            return width;
        };
        this.height = function () {
            return height;
        };
        this.centroidDistance = function (cd) {
            if (!arguments.length) return centroidDistance;
            centroidDistance = cd;
        };
        this.cmp = function (rect) {
            if (centroidDistance < rect.centroidDistance()) return -1;
            if (centroidDistance > rect.centroidDistance()) return 0;
            return 0;
        };
        this.add = function (rect) {
            var tmpx = Math.min(that.minX(), rect.minX());
            var tmpy = Math.min(that.minY(), rect.minY());
            var maxX = Math.max(that.maxX(), rect.maxX());
            var maxY = Math.max(that.maxY(), rect.maxY());
            x = tmpx;
            y = tmpy;
            width = maxX - x;
            height = maxY - y;
        };
        this.contains = function (p) {
            var px = p.x();
            var py = p.y();
            return that.containsPt(px, py);
        };
        this.containsPt = function (px, py) {
            if (px < x || px >= x + width) return false;
            return !(py < y || py >= y + height);
        };
        this.intersects = function (rect) {
            if (that.width() <= 0 || that.height() <= 0 || rect.width() <= 0 || rect.height() <= 0) return false;
            return (rect.maxX() > that.minX() &&
                rect.maxY() > that.minY() &&
                rect.minX() < that.maxX() &&
                rect.minY() < that.maxY());
        };
        this.intersectsLine = function (line) {
            var x1 = line.x1();
            var y1 = line.y1();
            var x2 = line.x2();
            var y2 = line.y2();
            // taken from JDK 8 java.awt.geom.FilledElement2D.Double#intersectsLine(double, double, double, double)
            var out1;
            var out2;
            if ((out2 = that.outcode(x2, y2)) === 0) {
                return true;
            }
            while ((out1 = that.outcode(x1, y1)) !== 0) {
                if ((out1 & out2) !== 0) {
                    return false;
                }
                if ((out1 & (FilledElement.OUT_LEFT | FilledElement.OUT_RIGHT)) !== 0) {
                    var x = that.minX();
                    if ((out1 & FilledElement.OUT_RIGHT) !== 0) {
                        x += that.width();
                    }
                    y1 = y1 + (x - x1) * (y2 - y1) / (x2 - x1);
                    x1 = x;
                } else {
                    var y = that.minY();
                    if ((out1 & FilledElement.OUT_BOTTOM) !== 0) {
                        y += that.height();
                    }
                    x1 = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
                    y1 = y;
                }
            }
            return true;
        };
        this.outcode = function (px, py) {
            // taken from JDK 8 java.awt.geom.FilledElement2D.Double#outcode(double, double)
            var out = 0;
            if (width <= 0) {
                out |= FilledElement.OUT_LEFT | FilledElement.OUT_RIGHT;
            } else if (px < x) {
                out |= FilledElement.OUT_LEFT;
            } else if (px > x + width) {
                out |= FilledElement.OUT_RIGHT;
            }
            if (height <= 0) {
                out |= FilledElement.OUT_TOP | FilledElement.OUT_BOTTOM;
            } else if (py < y) {
                out |= FilledElement.OUT_TOP;
            } else if (py > y + height) {
                out |= FilledElement.OUT_BOTTOM;
            }
            return out;
        };
        if (arguments.length && _rect) {
            this.rect(_rect);
        }
    } // FilledElement
    FilledElement.prototype.toString = function () {
        return "FilledElement[x=" + this.minX() + ", y=" + this.minY() + ", w=" + this.width() + ", h=" + this.height() + "]";
    };
    FilledElement.OUT_LEFT = 1;
    FilledElement.OUT_TOP = 2;
    FilledElement.OUT_RIGHT = 4;
    FilledElement.OUT_BOTTOM = 8;

    function Line(_x1, _y1, _x2, _y2) {
        var that = this;
        var x1 = +_x1;
        var y1 = +_y1;
        var x2 = +_x2;
        var y2 = +_y2;

        this.rect = function () {
            var minX = Math.min(x1, x2);
            var minY = Math.min(y1, y2);
            var maxX = Math.max(x1, x2);
            var maxY = Math.max(y1, y2);
            var res = new FilledElement({
                x: minX,
                y: minY,
                width: maxX - minX,
                height: maxY - minY,
            });
            return res;
        }
        this.x1 = function (_) {
            if (!arguments.length) return x1;
            x1 = _;
        };
        this.x2 = function (_) {
            if (!arguments.length) return x2;
            x2 = _;
        };
        this.y1 = function (_) {
            if (!arguments.length) return y1;
            y1 = _;
        };
        this.y2 = function (_) {
            if (!arguments.length) return y2;
            y2 = _;
        };
        // whether an infinite line to positive x from the point p will cut through the line
        this.cuts = function (p) {
            if (y1 === y2) return false;
            var y = p.y();
            if ((y < y1 && y <= y2) || (y > y1 && y >= y2)) return false;
            var x = p.x();
            if (x > x1 && x >= x2) return false;
            if (x < x1 && x <= x2) return true;
            var cross = x1 + (y - y1) * (x2 - x1) / (y2 - y1);
            return x <= cross;
        };
        this.ptSegDistSq = function (x, y) {
            return OutlineGenerator.linePtSegDistSq(x1, y1, x2, y2, x, y);
        };
        this.ptClose = function (x, y, r) {
            // check whether the point is outside the bounding rectangle with padding r
            if (x1 < x2) {
                if (x < x1 - r || x > x2 + r) return false;
            } else {
                if (x < x2 - r || x > x1 + r) return false;
            }
            if (y1 < y2) {
                if (y < y1 - r || y > y2 + r) return false;
            } else {
                if (y < y2 - r || y > y1 + r) return false;
            }
            return true;
        };
    } // Line
    function Area(width, height) {
        var size = width * height;
        var buff = new Float32Array(size);

        this.bound = function (pos, isX) {
            if (pos < 0) return 0;
            return Math.min(pos, (isX ? width : height) - 1);
        };
        this.get = function (x, y) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return Number.NaN;
            }
            return buff[x + y * width];
        };
        this.set = function (x, y, v) {
            if (x < 0 || x >= width || y < 0 || y >= height) {
                return;
            }
            buff[x + y * width] = v;
        };
        this.width = function () {
            return width;
        };
        this.height = function () {
            return height;
        };
    } // Area
    function Intersection(p, s) {
        var point = p;
        var state = s;

        this.getState = function () {
            return state;
        };
        this.getPoint = function () {
            return point;
        };
    } // Intersection
    Intersection.POINT = 1;
    Intersection.PARALLEL = 2;
    Intersection.COINCIDENT = 3;
    Intersection.NONE = 4;
    Intersection.intersectLineLine = function (la, lb) {
        var uaT = (lb.x2() - lb.x1()) * (la.y1() - lb.y1())
            - (lb.y2() - lb.y1()) * (la.x1() - lb.x1());
        var ubT = (la.x2() - la.x1()) * (la.y1() - lb.y1())
            - (la.y2() - la.y1()) * (la.x1() - lb.x1());
        var uB = (lb.y2() - lb.y1()) * (la.x2() - la.x1())
            - (lb.x2() - lb.x1()) * (la.y2() - la.y1());
        if (uB) {
            var ua = uaT / uB;
            var ub = ubT / uB;
            if (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1) {
                var p = new Point(la.x1() + ua * (la.x2() - la.x1()), la.y1() + ua * (la.y2() - la.y1()));
                return new Intersection(p, Intersection.POINT);
            }
            return new Intersection(null, Intersection.NONE);
        }
        return new Intersection(null, (uaT === 0 || ubT === 0) ? Intersection.COINCIDENT : Intersection.PARALLEL);
    };
    Intersection.fractionAlongLineA = function (la, lb) {
        var uaT = (lb.x2() - lb.x1()) * (la.y1() - lb.y1())
            - (lb.y2() - lb.y1()) * (la.x1() - lb.x1());
        var ubT = (la.x2() - la.x1()) * (la.y1() - lb.y1())
            - (la.y2() - la.y1()) * (la.x1() - lb.x1());
        var uB = (lb.y2() - lb.y1()) * (la.x2() - la.x1())
            - (lb.x2() - lb.x1()) * (la.y2() - la.y1());
        if (uB) {
            var ua = uaT / uB;
            var ub = ubT / uB;
            if (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1) {
                return ua;
            }
        }
        return Number.POSITIVE_INFINITY;
    };
    // we can move them out here since there can't be any concurrency
    var intersectionPline = new Line(0.0, 0.0, 0.0, 0.0);
    Intersection.fractionToLineCenter = function (bounds, line) {
        var minDistance = Number.POSITIVE_INFINITY;
        var countIntersections = 0;

        function testLine(xa, ya, xb, yb) {
            intersectionPline.x1(xa);
            intersectionPline.y1(ya);
            intersectionPline.x2(xb);
            intersectionPline.y2(yb);
            var testDistance = Intersection.fractionAlongLineA(line, intersectionPline);
            testDistance = Math.abs(testDistance - 0.5);
            if ((testDistance >= 0) && (testDistance <= 1)) {
                countIntersections += 1;
                if (testDistance < minDistance) {
                    minDistance = testDistance;
                }
            }
        }

        // top
        testLine(bounds.minX(), bounds.minY(), bounds.maxX(), bounds.minY());
        // left
        testLine(bounds.minX(), bounds.minY(), bounds.minX(), bounds.maxY());
        if (countIntersections > 1) return minDistance;
        // bottom
        testLine(bounds.minX(), bounds.maxY(), bounds.maxX(), bounds.maxY());
        if (countIntersections > 1) return minDistance;
        // right
        testLine(bounds.maxX(), bounds.minY(), bounds.maxX(), bounds.maxY());
        // if no intersection, return -1
        if (countIntersections === 0) return -1;
        return minDistance;
    };
    Intersection.fractionToLineEnd = function (bounds, line) {
        var minDistance = Number.POSITIVE_INFINITY;
        var countIntersections = 0;

        function testLine(xa, ya, xb, yb) {
            var testDistance = Intersection.fractionAlongLineA(line, new Line(xa, ya, xb, yb));
            if ((testDistance >= 0) && (testDistance <= 1)) {
                countIntersections += 1;
                if (testDistance < minDistance) {
                    minDistance = testDistance;
                }
            }
        }

        // top
        testLine(bounds.minX(), bounds.minY(), bounds.maxX(), bounds.minY());
        // left
        testLine(bounds.minX(), bounds.minY(), bounds.minX(), bounds.maxY());
        if (countIntersections > 1) return minDistance;
        // bottom
        testLine(bounds.minX(), bounds.maxY(), bounds.maxX(), bounds.maxY());
        if (countIntersections > 1) return minDistance;
        // right
        testLine(bounds.maxX(), bounds.minY(), bounds.maxX(), bounds.maxY());
        // if no intersection, return -1
        if (countIntersections === 0) return -1;
        return minDistance;
    };
    Intersection.testIntersection = function (line, bounds, intersections) {
        var countIntersections = 0;

        function fillIntersection(ix, xa, ya, xb, yb) {
            intersections[ix] = Intersection.intersectLineLine(line, new Line(xa, ya, xb, yb));
            if (intersections[ix].getState() === Intersection.POINT) {
                countIntersections += 1;
            }
        }

        // top
        fillIntersection(0, bounds.minX(), bounds.minY(), bounds.maxX(), bounds.minY());
        // left
        fillIntersection(1, bounds.minX(), bounds.minY(), bounds.minX(), bounds.maxY());
        // bottom
        fillIntersection(2, bounds.minX(), bounds.maxY(), bounds.maxX(), bounds.maxY());
        // right
        fillIntersection(3, bounds.maxX(), bounds.minY(), bounds.maxX(), bounds.maxY());
        return countIntersections;
    };

    function MarchingSquares(contour, potentialArea, step, t) {
        var direction = MarchingSquares.S;
        var threshold = t;
        var marched = false;

        function updateDir(x, y, dir, res) {
            var v = potentialArea.get(x, y);
            if (isNaN(v)) return v;
            if (v > threshold) return dir + res;
            return dir;
        }

        function getState(x, y) {
            var dir = 0;
            dir = updateDir(x, y, dir, 1);
            dir = updateDir(x + 1, y, dir, 2);
            dir = updateDir(x, y + 1, dir, 4);
            dir = updateDir(x + 1, y + 1, dir, 8);
            if (isNaN(dir)) {
                return -1;
            }
            return dir;
        }

        function doMarch(xpos, ypos) {
            var x = xpos;
            var y = ypos;
            for (; ;) { // iterative version of end recursion
                var p = new Point(x * step, y * step);
                // check if we're back where we started
                if (contour.contains(p)) {
                    if (!contour.isFirst(p)) {
                        // encountered a loop but haven't returned to start; will change
                        // direction using conditionals and continue back to start
                    } else {
                        return true;
                    }
                } else {
                    contour.add(p);
                }
                var state = getState(x, y);
                // x, y are upper left of 2X2 marching square
                switch (state) {
                    case -1:
                        return true; // Marched out of bounds
                    case 0:
                    case 3:
                    case 2:
                    case 7:
                        direction = MarchingSquares.E;
                        break;
                    case 12:
                    case 14:
                    case 4:
                        direction = MarchingSquares.W;
                        break;
                    case 6:
                        direction = (direction === MarchingSquares.N) ? MarchingSquares.W : MarchingSquares.E;
                        break;
                    case 1:
                    case 13:
                    case 5:
                        direction = MarchingSquares.N;
                        break;
                    case 9:
                        direction = (direction === MarchingSquares.E) ? MarchingSquares.N : MarchingSquares.S;
                        break;
                    case 10:
                    case 8:
                    case 11:
                        direction = MarchingSquares.S;
                        break;
                    default:
                        return true;
                }

                switch (direction) {
                    case MarchingSquares.N:
                        y -= 1; // up
                        break;
                    case MarchingSquares.S:
                        y += 1; // down
                        break;
                    case MarchingSquares.W:
                        x -= 1; // left
                        break;
                    case MarchingSquares.E:
                        x += 1; // right
                        break;
                    default:
                        return true;
                }
            }
            return true;
        }

        this.march = function () {
            for (var x = 0; x < potentialArea.width() && !marched; x += 1) {
                for (var y = 0; y < potentialArea.height() && !marched; y += 1) {
                    if (potentialArea.get(x, y) > threshold && getState(x, y) != 15) {
                        marched = doMarch(x, y);
                    }
                }
            }
            return marched;
        };
    } // MarchingSquares
    MarchingSquares.N = 0;
    MarchingSquares.S = 1;
    MarchingSquares.E = 2;
    MarchingSquares.W = 3;
    var maxRoutingIterations = 100;
    var maxMarchingIterations = 20;
    var edgeR0 = 10;
    var edgeR1 = 20;
    var nodeR0 = 20;
    var morphBuffer = 15;
    var skip = 8;

    this.squarePixel = function (_) {
        if (!arguments.length) return squarePixel;
        squarePixel = _;
    };
    this.edgeR0 = function (_) {
        if (!arguments.length) return edgeR0;
        edgeR0 = _;
    };
    this.outlineWidth = function (_) {
        if (!arguments.length) return outlineWidth;
        outlineWidth = _;
    };
    this.morphBuffer = function (_) {
        if (!arguments.length) return morphBuffer;
        morphBuffer = _;
    };
    this.skip = function (_) {
        if (!arguments.length) return skip;
        skip = _;
    };

    var threshold = 1;
    var nodeInfluenceFactor = 1;
    var edgeInfluenceFactor = 1;
    var negativeNodeInfluenceFactor = -0.8;
    var activeRegion = null;
    var virtualEdges = [];
    var potentialArea = null;

    var lastThreshold = Number.NaN;

    this.createOutline = function (members) {
        if (!members.length) return [];

        var memberItems = members.map(function (m) {
            return new FilledElement(m);
        });
        calculateVirtualEdges(memberItems);
        activeRegion = null;
        memberItems.forEach(function (m) {
            if (!activeRegion) {
                activeRegion = new FilledElement(m.rect());
            } else {
                activeRegion.add(m);
            }
        });
        virtualEdges.forEach(function (l) {
            activeRegion.add(l.rect());
        });

        activeRegion.rect({
            x: activeRegion.minX() - Math.max(edgeR1, outlineWidth) - morphBuffer,
            y: activeRegion.minY() - Math.max(edgeR1, outlineWidth) - morphBuffer,
            width: activeRegion.width() + 2 * Math.max(edgeR1, outlineWidth) + 2 * morphBuffer,
            height: activeRegion.height() + 2 * Math.max(edgeR1, outlineWidth) + 2 * morphBuffer,
        });
        //console.log(activeRegion);
        potentialArea = new Area(Math.ceil(activeRegion.width() / squarePixel), Math.ceil(activeRegion.height() / squarePixel));

        var estLength = (Math.floor(activeRegion.width()) + Math.floor(activeRegion.height())) * 2;
        var surface = new PointList(estLength);

        var tempThreshold = threshold;
        var tempNegativeNodeInfluenceFactor = negativeNodeInfluenceFactor;
        var tempNodeInfluenceFactor = nodeInfluenceFactor;
        var tempEdgeInfluenceFactor = edgeInfluenceFactor;

        var iterations = 0;

        // add the aggregate and all it's members and virtual edges
        fillPotentialArea(activeRegion, memberItems, potentialArea);

        // try to march, check if surface contains all items
        while ((!calculateContour(surface, activeRegion, memberItems, potentialArea)) && (iterations < maxMarchingIterations)) {
            surface.clear();
            iterations += 1;

            // reduce negative influences first; this will allow the surface to
            // pass without making it fatter all around (which raising the threshold does)
            if (iterations <= maxMarchingIterations * 0.5) {
                if (negativeNodeInfluenceFactor != 0) {
                    threshold *= 0.95;
                    negativeNodeInfluenceFactor *= 0.8;
                    fillPotentialArea(activeRegion, memberItems, potentialArea);
                }
            }

            // after half the iterations, start increasing positive energy and lowering the threshold
            if (iterations > maxMarchingIterations * 0.5) {
                threshold *= 0.95;
                nodeInfluenceFactor *= 1.2;
                edgeInfluenceFactor *= 1.2;
                fillPotentialArea(activeRegion, memberItems, potentialArea);
            }
        }

        lastThreshold = threshold;
        threshold = tempThreshold;
        negativeNodeInfluenceFactor = tempNegativeNodeInfluenceFactor;
        nodeInfluenceFactor = tempNodeInfluenceFactor;
        edgeInfluenceFactor = tempEdgeInfluenceFactor;

        // start with global SKIP value, but decrease skip amount if there aren't enough points in the surface
        var thisSkip = skip;
        // prepare viz attribute array
        var size = surface.size;
        //console.log(size);
        if (thisSkip > 1) {
            size = Math.floor(surface.size() / thisSkip);
            // if we reduced too much (fewer than three points in reduced surface) reduce skip and try again
            while ((size < 3) && (thisSkip > 1)) {
                thisSkip -= 1;
                size = Math.floor(surface.size() / thisSkip);
            }
        }

        // add the offset of the active area to the coordinates
        var xcorner = activeRegion.minX();
        var ycorner = activeRegion.minY();

        var fhull = new PointList(size);
        // copy hull values
        var tempList = [[45, 45], [175, 185], [85, 95], [155, 145], [115, 125], [145, 155], [205, 215], [235, 245],
            [215, 205], [55, 65], [185, 175], [255, 255], [125, 115], [95, 85], [235, 245], [65, 55], [65, 54]]
        // size=tempList.length
        // for(var i = 0, j = 0;j < size;j += 1,i += thisSkip) {
        //   console.log(j)
        //   console.log(i);
        //   console.log(tempList[i]);
        //   fhull.add(new Point(tempList[i][0], tempList[i][1]));
        // }
        for (var i = 0, j = 0; j < size; j += 1, i += thisSkip) {
            fhull.add(new Point(surface.get(i).x() + xcorner, surface.get(i).y() + ycorner));
        }


        //console.log(fhull)
        return fhull.list();
    };


    function calculateContour(contour, bounds, members, potentialArea) {
        // if no surface could be found stop
        if (!new MarchingSquares(contour, potentialArea, squarePixel, threshold).march()) return false;
        return testContainment(contour, bounds, members)[0];
    }

    function testContainment(contour, bounds, members) {
        // precise bounds checking
        // copy hull values
        var g = [];
        var gbounds = null;

        function contains(g, p) {
            var line = null;
            var first = null;
            var crossings = 0;
            g.forEach(function (cur) {
                if (!line) {
                    line = new Line(cur.x(), cur.y(), cur.x(), cur.y());
                    first = cur;
                    return;
                }
                line.x1(line.x2());
                line.y1(line.y2());
                line.x2(cur.x());
                line.y2(cur.y());
                if (line.cuts(p)) {
                    crossings += 1;
                }
            });
            if (first) {
                line.x1(line.x2());
                line.y1(line.y2());
                line.x2(first.x());
                line.y2(first.y());
                if (line.cuts(p)) {
                    crossings += 1;
                }
            }
            return crossings % 2 === 1;
        }

        // start with global SKIP value, but decrease skip amount if there
        // aren't enough points in the surface
        var thisSkip = skip;
        // prepare viz attribute array
        var size = contour.size();
        if (thisSkip > 1) {
            size = contour.size() / thisSkip;
            // if we reduced too much (fewer than three points in reduced surface) reduce skip and try again
            while ((size < 3) && (thisSkip > 1)) {
                thisSkip--;
                size = contour.size() / thisSkip;
            }
        }

        var xcorner = bounds.minX();
        var ycorner = bounds.minY();

        // simulate the surface we will eventually draw, using straight segments (approximate, but fast)
        for (var i = 0; i < size - 1; i += 1) {
            var px = contour.get(i * thisSkip).x() + xcorner;
            var py = contour.get(i * thisSkip).y() + ycorner;
            var r = {
                x: px,
                y: py,
                width: 0,
                height: 0
            };
            if (!gbounds) {
                gbounds = new FilledElement(r);
            } else {
                gbounds.add(new FilledElement(r));
            }
            g.push(new Point(px, py));
        }

        var containsAll = true;
        var containsExtra = false;
        if (gbounds) {
            members.forEach(function (item) {
                var p = new Point(item.centerX(), item.centerY());
                // check rough bounds
                containsAll = containsAll && gbounds.contains(p);
                // check precise bounds if rough passes
                containsAll = containsAll && contains(g, p);
            });
        }
        return [containsAll, containsExtra];
    }

    function fillPotentialArea(activeArea, members, potentialArea) {
        var influenceFactor = 0;
        // add all positive energy (included items) first, as negative energy
        // (morphing) requires all positives to be already set
        if (nodeInfluenceFactor) {
            members.forEach(function (item) {
                // add node energy
                influenceFactor = nodeInfluenceFactor;
                var nodeRDiff = nodeR0 - outlineWidth;
                // using inverse a for numerical stability
                var inva = nodeRDiff * nodeRDiff;
                calculateFilledElementInfluence(potentialArea, influenceFactor / inva, outlineWidth, item);
            }); // end processing node items of this aggregate
        } // end processing positive node energy

        if (edgeInfluenceFactor) {
            // add the influence of all the virtual edges
            influenceFactor = edgeInfluenceFactor;
            var inva = ((edgeR0 - edgeR1) * (edgeR0 - edgeR1));

            if (virtualEdges.length > 0) {
                calculateLinesInfluence(potentialArea, influenceFactor / inva, edgeR1, virtualEdges, activeArea);
            }
        }
    }

    function calculateCentroidDistances(items) {
        var totalx = 0;
        var totaly = 0;
        var nodeCount = 0;
        items.forEach(function (item) {
            totalx += item.centerX();
            totaly += item.centerY();
            nodeCount += 1;
        });
        totalx /= nodeCount;
        totaly /= nodeCount;
        items.forEach(function (item) {
            var diffX = totalx - item.centerX();
            var diffY = totaly - item.centerY();
            item.centroidDistance(Math.sqrt(diffX * diffX + diffY * diffY));
        });
    }

    function calculateVirtualEdges(items) {
        var visited = [];
        virtualEdges = [];
        calculateCentroidDistances(items);
        items.sort(function (a, b) {
            return a.cmp(b);
        });

        items.forEach(function (item) {
            var lines = connectItem(item, visited);
            lines.forEach(function (l) {
                virtualEdges.push(l);
            });
            visited.push(item);
        });
    }

    function connectItem(item, visited) {
        var scannedLines = [];
        var linesToCheck = [];

        var itemCenter = new Point(item.centerX(), item.centerY());
        var closestNeighbour = null;
        var minLengthSq = Number.POSITIVE_INFINITY;
        // discover the nearest neighbour with minimal interference items
        visited.forEach(function (neighbourItem) {
            var nCenter = new Point(neighbourItem.centerX(), neighbourItem.centerY());
            var distanceSq = itemCenter.distanceSq(nCenter);

            var completeLine = new Line(itemCenter.x(), itemCenter.y(), nCenter.x(), nCenter.y());
            // augment distance by number of interfering items
            let numberInterferenceItems = 0;

            if (distanceSq * (numberInterferenceItems + 1) * (numberInterferenceItems + 1) < minLengthSq) {
                closestNeighbour = neighbourItem;
                minLengthSq = distanceSq * (numberInterferenceItems + 1) * (numberInterferenceItems + 1);
            }
        });

        // if there is a visited closest neighbour, add straight line between
        // them to the positive energy to ensure connected clusters
        if (closestNeighbour) {
            var completeLine = new Line(itemCenter.x(), itemCenter.y(), closestNeighbour.centerX(), closestNeighbour.centerY());
            // route the edge around intersecting nodes not in set
            linesToCheck.push(completeLine);

            var hasIntersection = true;
            var iterations = 0;
            var intersections = [];
            intersections.length = 4;

            while (hasIntersection && iterations < maxRoutingIterations) {
                hasIntersection = false;
                while (!hasIntersection && linesToCheck.length) {
                    var line = linesToCheck.pop();
                    // resolve intersections in order along edge

                    // no intersection found, mark this line as completed
                    if (!hasIntersection) {
                        scannedLines.push(line);
                    }
                    iterations += 1;
                } // end inner loop - out of lines or found an intersection
            } // end outer loop - no more intersections or out of iterations

            // finalize any that were not rerouted (due to running out of
            // iterations) or if we aren't morphing
            while (linesToCheck.length) {
                scannedLines.push(linesToCheck.pop());
            }

            // try to merge consecutive lines if possible

            scannedLines = linesToCheck;
        }
        return scannedLines;
    }

    function calculateLinesInfluence(potentialArea, influenceFactor, r1, lines, activeRegion) {
        lines.forEach(function (line) {
            var lr = line.rect();
            // only traverse the plausible area
            var startX = potentialArea.bound(Math.floor((lr.minX() - r1 - activeRegion.minX()) / squarePixel), true);
            var startY = potentialArea.bound(Math.floor((lr.minY() - r1 - activeRegion.minY()) / squarePixel), false);
            var endX = potentialArea.bound(Math.ceil((lr.maxX() + r1 - activeRegion.minX()) / squarePixel), true);
            var endY = potentialArea.bound(Math.ceil((lr.maxY() + r1 - activeRegion.minY()) / squarePixel), false);
            // for every point in active part of potentialArea, calculate distance to nearest point on line and add influence
            for (var y = startY; y < endY; y += 1) {
                for (var x = startX; x < endX; x += 1) {
                    // if we are adding negative energy, skip if not already
                    // positive; positives have already been added first, and adding
                    // negative to <=0 will have no affect on surface
                    if (influenceFactor < 0 && potentialArea.get(x, y) <= 0) {
                        continue;
                    }
                    // convert back to screen coordinates
                    var tempX = x * squarePixel + activeRegion.minX();
                    var tempY = y * squarePixel + activeRegion.minY();
                    var minDistanceSq = line.ptSegDistSq(tempX, tempY);
                    // only influence if less than r1
                    if (minDistanceSq < r1 * r1) {
                        var mdr = Math.sqrt(minDistanceSq) - r1;
                        potentialArea.set(x, y, potentialArea.get(x, y) + influenceFactor * mdr * mdr);
                    }
                }
            }
        });
    }

    function getRectDistSq(rect, tempX, tempY) {
        // test current point to see if it is inside rectangle
        if (!rect.containsPt(tempX, tempY)) {
            // which edge of rectangle is closest
            var outcode = rect.outcode(tempX, tempY);
            // top
            if ((outcode & FilledElement.OUT_TOP) === FilledElement.OUT_TOP) {
                // and left
                if ((outcode & FilledElement.OUT_LEFT) === FilledElement.OUT_LEFT) {
                    // linear distance from upper left corner
                    return Point.ptsDistanceSq(tempX, tempY, rect.minX(), rect.minY());
                } else {
                    // and right
                    if ((outcode & FilledElement.OUT_RIGHT) === FilledElement.OUT_RIGHT) {
                        // linear distance from upper right corner
                        return Point.ptsDistanceSq(tempX, tempY, rect.maxX(), rect.minY());
                    } else {
                        // distance from top line segment
                        return (rect.minY() - tempY) * (rect.minY() - tempY);
                    }
                }
            } else {
                // bottom
                if ((outcode & FilledElement.OUT_BOTTOM) === FilledElement.OUT_BOTTOM) {
                    // and left
                    if ((outcode & FilledElement.OUT_LEFT) === FilledElement.OUT_LEFT) {
                        // linear distance from lower left corner
                        return Point.ptsDistanceSq(tempX, tempY, rect.minX(), rect.maxY());
                    } else {
                        // and right
                        if ((outcode & FilledElement.OUT_RIGHT) === FilledElement.OUT_RIGHT) {
                            // linear distance from lower right corner
                            return Point.ptsDistanceSq(tempX, tempY, rect.maxX(), rect.maxY());
                        } else {
                            // distance from bottom line segment
                            return (tempY - rect.maxY()) * (tempY - rect.maxY());
                        }
                    }
                } else {
                    // left only
                    if ((outcode & FilledElement.OUT_LEFT) === FilledElement.OUT_LEFT) {
                        // linear distance from left edge
                        return (rect.minX() - tempX) * (rect.minX() - tempX);
                    } else {
                        // right only
                        if ((outcode & FilledElement.OUT_RIGHT) === FilledElement.OUT_RIGHT) {
                            // linear distance from right edge
                            return (tempX - rect.maxX()) * (tempX - rect.maxX());
                        }
                    }
                }
            }
        }
        return 0;
    }

    function calculateFilledElementInfluence(potentialArea, influenceFactor, r1, rect) {
        // find the affected subregion of potentialArea
        var startX = potentialArea.bound(Math.floor((rect.minX() - r1 - activeRegion.minX()) / squarePixel), true);
        var startY = potentialArea.bound(Math.floor((rect.minY() - r1 - activeRegion.minY()) / squarePixel), false);
        var endX = potentialArea.bound(Math.ceil((rect.maxX() + r1 - activeRegion.minX()) / squarePixel), true);
        var endY = potentialArea.bound(Math.ceil((rect.maxY() + r1 - activeRegion.minY()) / squarePixel), false);
        // for every point in active subregion of potentialArea, calculate
        // distance to nearest point on rectangle and add influence
        for (var y = startY; y < endY; y += 1) {
            for (var x = startX; x < endX; x += 1) {
                // convert back to screen coordinates
                var tempX = x * squarePixel + activeRegion.minX();
                var tempY = y * squarePixel + activeRegion.minY();
                var distanceSq = getRectDistSq(rect, tempX, tempY);
                // only influence if less than r1
                if (distanceSq < r1 * r1) {
                    var dr = Math.sqrt(distanceSq) - r1;
                    potentialArea.set(x, y, potentialArea.get(x, y) + influenceFactor * dr * dr);
                }
            }
        }
    }
} // OutlineGenerator

OutlineGenerator.linePtSegDistSq = function (lx1, ly1, lx2, ly2, x, y) {

    var x1 = lx1;
    var y1 = ly1;
    var x2 = lx2 - x1;
    var y2 = ly2 - y1;
    var px = x - x1;
    var py = y - y1;
    var dotprod = px * x2 + py * y2;
    var projlenSq;
    if (dotprod <= 0) {
        projlenSq = 0;
    } else {
        px = x2 - px;
        py = y2 - py;
        dotprod = px * x2 + py * y2;
        if (dotprod <= 0) {
            projlenSq = 0;
        } else {
            projlenSq = dotprod * dotprod / (x2 * x2 + y2 * y2);
        }
    }
    var lenSq = px * px + py * py - projlenSq;
    if (lenSq < 0) {
        lenSq = 0;
    }
    return lenSq;
};

OutlineGenerator.prototype.setRange = function (range) {
    this.edgeR0()
}
export {OutlineGenerator};
