import React, {useEffect, useRef, useState} from 'react';
import axios from 'axios';
import * as d3 from 'd3';
import './Graph.css';

interface Edge {
    source: string;
    target: string;
}

interface Node {
    id: string;
    x: number;
    y: number;
    type: string;
}

interface GraphResponse {
    edges: Edge[];
    nodes: Node[];
    // data: {
    // }
}

interface EdgeContextMenu {
    edge: Edge;
    x: number;
    y: number;
}

interface NodeContextMenu {
    node: Node;
    x: number;
    y: number;
}

interface ErrorResponse {
    response?: {
        data?: {
            details?: string;
        };
    };
    message?: string;
}

const Graph = () => {

    const lexeme = 'LEXEME';
    const division = 'DIVISION';
    const opposition = 'OPPOSITION';

    const colours = new Map();
    colours.set('LEXEME', 'blue');
    colours.set('DIVISION', 'silver');
    colours.set('OPPOSITION', 'lightgray');

    const svgRef = useRef<SVGSVGElement | null>(null);
    const [nodes, setNodes] = useState<Node[]>([]);
    const [edges, setEdges] = useState<Edge[]>([]);
    const [selectedNode, setSelectedNode] = useState<Node | null>(null);
    const [edgeContextMenu, setEdgeContextMenu] = useState<EdgeContextMenu | null>(null);
    const [nodeContextMenu, setNodeContextMenu] = useState<NodeContextMenu | null>(null);
    const [menuOpen, setMenuOpen, ] = useState(false);
    const [viewBox, setViewBox] = useState({
        x: 0,
        y: 0,
        width: window.innerWidth,
        height: window.innerHeight,
    });

    // Adds the handleKeyDown event listener and its cleanup function only once after the initial render
    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            const step = 10;
            switch (e.key) {
                case 'ArrowUp':
                    setViewBox((prev) => ({ ...prev, y: prev.y - step }));
                    break;
                case 'ArrowDown':
                    setViewBox((prev) => ({ ...prev, y: prev.y + step }));
                    break;
                case 'ArrowLeft':
                    setViewBox((prev) => ({ ...prev, x: prev.x - step }));
                    break;
                case 'ArrowRight':
                    setViewBox((prev) => ({ ...prev, x: prev.x + step }));
                    break;
                default:
                    return;
            }
        };

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, []);

    // Loads the graph only once after the initial render
    useEffect(() => {
        getGraph();
    }, []);

    //  Updates the view box attribute of the graph when the view box changes
    useEffect(() => {
        if (svgRef.current) {
            svgRef.current.setAttribute('viewBox', `${viewBox.x} ${viewBox.y} ${viewBox.width} ${viewBox.height}`);
        }
    }, [viewBox]);

    // Initializes an SVG using D3 by setting its attributes and styles.
    // Creates and updates its nodes and edges based on the data fetched from the back-end.
    // Finally, sets up event handlers for dragging nodes and displaying context menus.
    useEffect(() => {
        const svg = d3.select(svgRef.current)
            .attr('width', '9999')
            .attr('height', '9999')
            .style('background', '#ffffff')
            .on('contextmenu', function (event) {
                event.preventDefault();
                const clickedOnNode = d3.select(event.target).classed('node');
                if (!clickedOnNode) {
                    createNode(event);
                }
            });

        const edge = svg.selectAll('.edge')
            .data(edges)
            .join('line')
            .attr('class', 'edge')
            .attr('stroke', '#999')
            .attr('stroke-dasharray', (d: Edge) => {
                const sourceNode = nodes.find((n: Node) => n.id === d.source);
                const targetNode = nodes.find((n: Node) => n.id === d.target);

                if (sourceNode && targetNode) {
                    return (sourceNode.type === lexeme && targetNode.type === lexeme) ? '5,5' : '0';
                }
                return '0';
            })
            .attr('x1', (d: Edge) => {
                const sourceNode = nodes.find((n: Node) => n.id === d.source);
                if (!sourceNode) {
                    console.error(`Source node with id ${d.source} not found`);
                    return NaN;
                }
                return sourceNode.x;
            })
            .attr('y1', (d: Edge) => {
                const sourceNode = nodes.find((n: Node) => n.id === d.source);
                if (!sourceNode) {
                    console.error(`Source node with id ${d.source} not found`);
                    return NaN;
                }
                return sourceNode.y;
            })
            .attr('x2', (d: Edge) => {
                const targetNode = nodes.find((n: Node) => n.id === d.target);
                if (!targetNode) {
                    console.error(`Target node with id ${d.target} not found`);
                    return NaN;
                }
                return targetNode.x;
            })
            .attr('y2', (d: Edge) => {
                const targetNode = nodes.find((n: Node) => n.id === d.target);
                if (!targetNode) {
                    console.error(`Target node with id ${d.target} not found`);
                    return NaN;
                }
                return targetNode.y;
            })
            .on('contextmenu', function (event: MouseEvent, d: Edge) {
                event.preventDefault();
                setEdgeContextMenu({edge: d, x: event.clientX, y: event.clientY});
            });

        const nodeGroup = svg.selectAll('.node-group')
            .data(nodes, (d: any) => d.id)
            .join('g')
            .attr('class', 'node-group')
            .attr('transform', (d: Node) => `translate(${d.x},${d.y})`)
            // @ts-ignore
            .call(d3.drag().on('start', dragStarted).on('drag', dragged).on('end', dragEnded));

        nodeGroup.append('path')
            .attr('class', 'node')
            .attr('d', d3.symbol().type(d3.symbolCircle).size(200)())
            .attr('fill', (d: Node) => colours.get(d.type))
            .on('contextmenu', function (event: MouseEvent, d: Node) {
                event.preventDefault();
                setNodeContextMenu({node: d, x: event.clientX, y: event.clientY});
            });

        nodeGroup.append('text')
            .attr('class', 'text')
            .attr('x', 15)
            .attr('y', 15)
            .text((d: Node) => d.type === lexeme ? d.id : '');

        function dragStarted(this: any) {
            d3.select(this).raise().attr('stroke', 'black');
        }

        function dragged(event: any, d: Node) {
            d.x = event.x;
            d.y = event.y;

            edge
                .attr('x1', (d: Edge) => {
                    const sourceNode = nodes.find((n: Node) => n.id === d.source);
                    if (!sourceNode) {
                        console.error(`Source node with id ${d.source} not found`);
                        return NaN;
                    }
                    return sourceNode.x;
                })
                .attr('y1', (d: Edge) => {
                    const sourceNode = nodes.find((n: Node) => n.id === d.source);
                    if (!sourceNode) {
                        console.error(`Source node with id ${d.source} not found`);
                        return NaN;
                    }
                    return sourceNode.y;
                })
                .attr('x2', (d: Edge) => {
                    const targetNode = nodes.find((n: Node) => n.id === d.target);
                    if (!targetNode) {
                        console.error(`Target node with id ${d.target} not found`);
                        return NaN;
                    }
                    return targetNode.x;
                })
                .attr('y2', (d: Edge) => {
                    const targetNode = nodes.find((n: Node) => n.id === d.target);
                    if (!targetNode) {
                        console.error(`Target node with id ${d.target} not found`);
                        return NaN;
                    }
                    return targetNode.y;
                })
                .attr('stroke', '#999');

            nodeGroup
                .attr('transform', (d: Node) => `translate(${d.x},${d.y})`)
                .attr('fill', (d: Node) => colours.get(d.type));
        }

        function dragEnded(this: any, d: Node) {
            d3.select(this).attr('stroke', null);
            updateNode(d);
        }

        return () => {
            d3.select('body').on('click.context-menu', null);
        };
    }, [nodes, edges, selectedNode]);

    ///////////////////////////////////////////////////////////////////////////
    // Context Menu Handler
    ///////////////////////////////////////////////////////////////////////////

    // Allows to create an edge
    const createEdgeMenuHandler = () => {
        if (nodeContextMenu != null) {
            createEdge(nodeContextMenu.node.id);
            setNodeContextMenu(null);
        }
    }

    // Allows to change the type of a node
    const changeTypeMenuHandler = (newType: string) => {
        if (nodeContextMenu != null) {
            updateNode({...nodeContextMenu.node, type: newType});
            setNodeContextMenu(null);
        }
    }

    // Allows to delete an edge
    const deleteEdgeMenuHandler = () => {
        if (nodeContextMenu != null) {
            deleteEdge(nodeContextMenu.node.id);
            setEdgeContextMenu(null);
        }
    }

    // Allows to delete a node
    const deleteNodeMenuHandler = () => {
        if (nodeContextMenu != null) {
            deleteNode(nodeContextMenu.node.id);
            setEdgeContextMenu(null);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // REST Calls
    ///////////////////////////////////////////////////////////////////////////

    // Calls the RESTful end-point that deletes the graph
    const clearGraph = () => {
        /* eslint-disable no-restricted-globals */
        if (!confirm('Do you want to clear the graph?')) {
            return;
        }
        axios.delete<GraphResponse>('http://localhost:8080/graph')
            .then(() => {
                setNodes([]);
                setEdges([]);
                setSelectedNode(null);
            })
            .catch(error => handleError('Failed to clear the graph', error));
    };

    // Calls the RESTful end-point that creates an edge
    const createEdge = (id: string) => {
        const target = prompt(`Enter the target's name`);
        if (target) {
            axios.post<GraphResponse>(`http://localhost:8080/edges/${id}/${target}`)
                .then(response => {
                    setGraph(response.data);
                })
                .catch(error => handleError('Failed to create the edge', error));
        }
    };

    // Calls the RESTful end-point that creates a node
    const createNode = (event: MouseEvent) => {
        const name = prompt(`Enter the new node's name`);
        if (name) {
            const [x, y] = d3.pointer(event);
            const node = {id: name, x, y, type: lexeme};
            axios.post<GraphResponse>('http://localhost:8080/nodes', node)
                .then(response => {
                    setGraph(response.data);
                })
                .catch(error => handleError('Failed to create the node', error));
        }
    };

    // Calls the RESTful end-point that deletes an edge
    const deleteEdge = (id: string) => {
        const target = prompt(`Enter the target's name`);
        if (target) {
            axios.delete<GraphResponse>(`http://localhost:8080/edges/${id}/${target}`)
                .then(response => {
                    setGraph(response.data);
                })
                .catch(error => handleError('Failed to delete the edge', error));
        }
    };

    // Calls the RESTful end-point that deletes a node
    const deleteNode = (id: string) => {
        /* eslint-disable no-restricted-globals */
        if (!confirm('Do you want to delete the node?')) {
            return;
        }
        axios.delete<GraphResponse>(`http://localhost:8080/nodes/${id}`)
            .then(response => {
                setGraph(response.data);
            })
            .catch(error => handleError('Failed to delete the node', error));
    };

    // Calls the RESTful end-point that returns the graph
    const getGraph = () => {
        axios.get<GraphResponse>('http://localhost:8080/graph')
            .then(response => {
                setGraph(response.data);
            })
            .catch(error => handleError('Failed to get the graph', error));
    };

    // Writes an error to the console and displays an alert dialog box
    const handleError = (operation: string, error: ErrorResponse) => {
        console.error(operation, error);
        if (error.response && error.response.data) {
            alert(`${operation}: ${error.response.data.details}`);
        } else {
            alert(`${operation}: ${error.message}`);
        }
    }

    // Calls the RESTful end-point that returns a simplified string representation of the graph
    // and downloads the plain text document
    const printGraph = () => {
        axios.get<GraphResponse>('http://localhost:8080/graph/printout/ens')
            .then(response => {
                const blob = new Blob([JSON.stringify(response.data)], {type: 'text/plain'});
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = 'graph.txt';
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
                URL.revokeObjectURL(url);
            })
            .catch(error => handleError('Failed to get the graph', error));
    };

    // set the nodes and edges from the graph document received from the back-end
    const setGraph = (response: GraphResponse) => {
        setNodes(response.nodes);
        setEdges(response.edges);
    }

    // Calls the RESTful end-point that updates a node
    const updateNode = (node: Node) => {
        axios.put<GraphResponse>(`http://localhost:8080/nodes/${node.id}`, node)
            .then(response => {
                setGraph(response.data);

            })
            .catch(error => handleError('Failed to update the node', error));
    }

    // Calls the RESTful end-point that returns the graph as a JSON document and downloads it
    const downloadGraph = () => {
        const graphData = {nodes, edges};
        const json = JSON.stringify(graphData, null, 2);
        const blob = new Blob([json], {type: 'application/json'});
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'graph.json';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
    };

    // Calls the RESTful end-point that uploads a graph as a JSON document from the local filesystem
    const uploadGraph = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (!file) {
            return;
        }

        const reader = new FileReader();
        reader.onload = (e) => {
            const graph = JSON.parse(e.target?.result as string);
            axios.put<GraphResponse>(`http://localhost:8080/graph/`, graph)
                .then(response => {
                    setGraph(response.data);
                })
                .catch(error => handleError('Failed to upload the graph', error));
        };
        reader.readAsText(file);
    };

    // Renders a graph container with a context menu for node and edge operations
    // and a burger menu to clear, print, download, and upload s graph.
    return (
        <div className="graph-container">
            {nodeContextMenu && (
                <div className="context-menu" style={{top: nodeContextMenu.y, left: nodeContextMenu.x}}>
                    {nodeContextMenu.node.type !== lexeme && (
                        <div onClick={() => changeTypeMenuHandler(lexeme)}>
                            Change type to Lexeme
                        </div>
                    )}
                    {nodeContextMenu.node.type !== division && (
                        <div onClick={() => changeTypeMenuHandler(division)}>
                            Change type to Division
                        </div>
                    )}
                    {nodeContextMenu.node.type !== opposition && (
                        <div onClick={() => changeTypeMenuHandler(opposition)}>
                            Change type to Opposition
                        </div>
                    )}
                    {(
                        <div onClick={() => createEdgeMenuHandler()}>
                            Create Edge
                        </div>
                    )}
                    {(
                        <div onClick={() => deleteNodeMenuHandler()}>
                            Delete Node
                        </div>
                    )}
                    {(
                        <div onClick={() => deleteEdgeMenuHandler()}>
                            Delete Edge
                        </div>
                    )}
                    <div onClick={() => setNodeContextMenu(null)}>
                        Close
                    </div>
                </div>
            )}
            <div className={`burger-menu ${menuOpen ? 'open' : ''}`} onClick={() => setMenuOpen(!menuOpen)}>
                <div className="bar"></div>
                <div className="bar"></div>
                <div className="bar"></div>
            </div>
            {menuOpen && (
                <div className="menu-items">
                    <div className="menu-item" onClick={clearGraph}>Clear Graph</div>
                    <div className="menu-item" onClick={printGraph}>Print Graph</div>
                    <div className="menu-item" onClick={downloadGraph}>Download JSON</div>
                    <div className="menu-item" onClick={() => document.getElementById('file-input')?.click()}>Upload
                        File
                    </div>
                    <input id="file-input" type="file" className="hidden-file-input" onChange={uploadGraph}/>
                </div>
            )}
            <svg ref={svgRef}></svg>
        </div>
    );
};

export default Graph;
