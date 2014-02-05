package pl.malkiewicz.wat.se.model;

public class Vertex {
	  final private String id;
	  private int x;
	  private int y;
	  private int status;
	  
	  
	  public Vertex(String id,int x, int y, int status) {
	    this.id = id;
	    this.x = x;
	    this.y = y;
	    this.status = status;
	  }
	  public String getId() {
	    return id;
	  }

	  
	  
	  @Override
	  public int hashCode() {
	    final int prime = 31;
	    int result = 1;
	    result = prime * result + ((id == null) ? 0 : id.hashCode());
	    return result;
	  }
	  
	  @Override
	  public boolean equals(Object obj) {
	    if (this == obj)
	      return true;
	    if (obj == null)
	      return false;
	    if (getClass() != obj.getClass())
	      return false;
	    Vertex other = (Vertex) obj;
	    if (id == null) {
	      if (other.id != null)
	        return false;
	    } else if (!id.equals(other.id))
	      return false;
	    return true;
	  }
	public int getStatus()
	{
		return status;
	}
	public void setStatus(int status)
	{
		this.status = status;
	}
	public int getX()
	{
		return x;
	}
	public void setX(int x)
	{
		this.x = x;
	}
	public int getY()
	{
		return y;
	}
	public void setY(int y)
	{
		this.y = y;
	}

	  
	} 
