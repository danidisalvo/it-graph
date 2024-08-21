import React, {useEffect, useRef, useState} from 'react';
import * as d3 from 'd3';
import axios from 'axios';
import './Graph.css';

const Graph = () => {

    const lexeme = 'LEXEME';
    const division = 'DIVISION';
    const opposition = 'OPPOSITION';

    const colours = new Map();
    colours.set('LEXEME', 'blue');
    colours.set('DIVISION', 'silver');
    colours.set('OPPOSITION', 'dimgray');

    const svgRef = useRef();
    const [nodes, setNodes] = useState([]);
    const [edges, setEdges] = useState([]);
    const [selectedNode, setSelectedNode] = useState(null);
    const [contextMenu, setContextMenu] = useState(null);
    const [menuOpen, setMenuOpen] = useState(false);

    useEffect(() => {
        getGraph();
    }, []);

    useEffect(() => {
        const svg = d3.select(svgRef.current)
            .attr('width', '100%')
            .attr('height', '100%')
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
            .attr('stroke', d => d.type === 'DOTTED' ? 'blue' : '#999')
            .attr('stroke-dasharray', d => d.type === 'DOTTED' ? '5,5' : '0')
            .attr('x1', d => nodes.find(n => n.id === d.source).x)
            .attr('y1', d => nodes.find(n => n.id === d.source).y)
            .attr('x2', d => nodes.find(n => n.id === d.target).x)
            .attr('y2', d => nodes.find(n => n.id === d.target).y)
            .on('contextmenu', function (event, d) {
                event.preventDefault();
                setContextMenu({type: 'edge', edge: d, x: event.clientX, y: event.clientY});
            });

        const nodeGroup = svg.selectAll('.node-group')
            .data(nodes, d => d.id)
            .join('g')
            .attr('class', 'node-group')
            .attr('transform', d => `translate(${d.x},${d.y})`)
            .call(d3.drag()
                .on('start', dragStarted)
                .on('drag', dragged)
                .on('end', dragEnded));

        nodeGroup.append('path')
            .attr('class', 'node')
            .attr('d', d3.symbol().type(d3.symbolCircle).size(200)())
            .attr('fill', d => colours.get(d.type))
            .on('contextmenu', function (event, d) {
                event.preventDefault();
                setContextMenu({type: 'node', node: d, x: event.clientX, y: event.clientY});
            });

        nodeGroup.append('text')
            .attr('class', 'text')
            .attr('x', 15)
            .attr('y', 15)
            .text(d => d.id);

        function dragStarted(event, d) {
            d3.select(this).raise().attr('stroke', 'black');
        }

        function dragged(event, d) {
            d.x = event.x;
            d.y = event.y;

            edge
                .attr('x1', d => nodes.find(n => n.id === d.source).x)
                .attr('y1', d => nodes.find(n => n.id === d.source).y)
                .attr('x2', d => nodes.find(n => n.id === d.target).x)
                .attr('y2', d => nodes.find(n => n.id === d.target).y)
                .attr('stroke', '#999');

            nodeGroup
                .attr('transform', d => `translate(${d.x},${d.y})`)
                .attr('fill', d => colours.get(d.type));
        }

        function dragEnded(event, d) {
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

    const createEdgeMenuHandler = () => {
        createEdge(contextMenu.node.id);
        setContextMenu(null);
    }

    const changeTypeMenuHandler = (newType) => {
        updateNode({...contextMenu.node, type: newType});
        setContextMenu(null);
    }

    const deleteEdgeMenuHandler = () => {
        deleteEdge(contextMenu.node.id);
        setContextMenu(null);
    }

    const deleteNodeMenuHandler = () => {
        deleteNode(contextMenu.node.id);
        setContextMenu(null);
    }

    ///////////////////////////////////////////////////////////////////////////
    // REST Calls
    ///////////////////////////////////////////////////////////////////////////

    const handleError = (operation, error) => {
        console.error(operation, error);
        if (error.response && error.response.data) {
            alert(`${operation}: ${error.response.data.details}`);
        } else {
            alert(`${operation}: ${error.message}`);
        }
    }

    const setGraph = (response) => {
        setNodes(response.data.nodes);
        setEdges(response.data.edges);
    }

    const createEdge = (id) => {
        const target = prompt(`Enter the target's name`);
        if (target) {
            axios.post(`http://localhost:8080/edges/${id}/${target}`)
                .then(response => {
                    setGraph(response);
                })
                .catch(error => handleError('Failed to create the edge', error));
        }
    };

    const createNode = (event) => {
        const name = prompt(`Enter the new node's name`);
        if (name) {
            const [x, y] = d3.pointer(event);
            const node = {id: name, x, y, type: lexeme};
            axios.post('http://localhost:8080/nodes', node)
                .then(response => {
                    setGraph(response);
                })
                .catch(error => handleError('Failed to create the node', error));
        }
    };

    const clearGraph = () => {
        /* eslint-disable no-restricted-globals */
        if (!confirm('Do you want to clear the graph?')) {
            return;
        }
        axios.delete('http://localhost:8080/graph')
            .then(() => {
                setNodes([]);
                setEdges([]);
                setSelectedNode(null);
            })
            .catch(error => handleError('Failed to clear the graph', error));
    };

    const deleteEdge = (id) => {
        const target = prompt(`Enter the target's name`);
        if (target) {
            axios.delete(`http://localhost:8080/edges/${id}/${target}`)
                .then(response => {
                    setGraph(response);
                })
                .catch(error => handleError('Failed to delete the edge', error));
        }
    };

    const deleteNode = (id) => {
        /* eslint-disable no-restricted-globals */
        if (!confirm('Do you want to delete the node?')) {
            return;
        }
        axios.delete(`http://localhost:8080/nodes/${id}`)
            .then(response => {
                setGraph(response);
            })
            .catch(error => handleError('Failed to delete the node', error));
    };

    const getGraph = () => {
        axios.get('http://localhost:8080/graph')
            .then(response => {
                setGraph(response);
            })
            .catch(error => handleError('Failed to get the graph', error));
    };

    const updateNode = (node) => {
        axios.put(`http://localhost:8080/nodes/${node.id}`, node)
            .then(response => {
                setGraph(response);

            })
            .catch(error => handleError('Failed to update the node', error));
    }

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

    const uploadGraph = (event) => {
        const file = event.target.files[0];
        const reader = new FileReader();
        reader.onload = (e) => {
            const graph = JSON.parse(e.target.result);
            axios.put(`http://localhost:8080/graph/`, graph)
                .then(response => {
                    setGraph(response);
                })
                .catch(error => handleError('Failed to upload the graph', error));
        };
        reader.readAsText(file);
    };

    return (
        <div className="graph-container">
            {contextMenu && (
                <div className="context-menu" style={{top: contextMenu.y, left: contextMenu.x}}>
                    {contextMenu.type === 'node' && contextMenu.node.type !== lexeme && (
                        <div onClick={() => changeTypeMenuHandler(lexeme)}>
                            Change type to Lexeme
                        </div>
                    )}
                    {contextMenu.type === 'node' && contextMenu.node.type !== division && (
                        <div onClick={() => changeTypeMenuHandler(division)}>
                            Change type to Division
                        </div>
                    )}
                    {contextMenu.type === 'node' && contextMenu.node.type !== opposition && (
                        <div onClick={() => changeTypeMenuHandler(opposition)}>
                            Change type to Opposition
                        </div>
                    )}
                    {contextMenu.type === 'node' && (
                        <div onClick={() => createEdgeMenuHandler()}>
                            Create Edge
                        </div>
                    )}
                    {contextMenu.type === 'node' && (
                        <div onClick={() => deleteNodeMenuHandler()}>
                            Delete Node
                        </div>
                    )}
                    {contextMenu.type === 'node' && (
                        <div onClick={() => deleteEdgeMenuHandler()}>
                            Delete Edge
                        </div>
                    )}
                    <div onClick={() => setContextMenu(null)}>
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
                    <div className="menu-item" onClick={downloadGraph}>Download JSON</div>
                    <div className="menu-item" onClick={() => document.getElementById('file-input').click()}>Upload File</div>
                    <input id="file-input" type="file" className="hidden-file-input" onChange={uploadGraph} />
                </div>
            )}
            <svg ref={svgRef}></svg>
        </div>
    );
};

export default Graph;
