package pl.malkiewicz.wat.se.model;

import java.util.List;

public class Graph
{
	private List<Vertex> vertexes;
	private List<Edge> edges;

	public Graph(List<Vertex> vertexes, List<Edge> edges)
	{
		this.vertexes = vertexes;
		this.edges = edges;
	}

	public List<Vertex> getVertexes()
	{
		return vertexes;
	}

	public List<Edge> getEdges()
	{
		return edges;
	}

}
