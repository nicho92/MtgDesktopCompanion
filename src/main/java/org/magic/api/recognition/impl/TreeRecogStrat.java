package org.magic.api.recognition.impl;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.magic.api.interfaces.abstracts.AbstractRecognitionStrategy;
import org.magic.services.recognition.DescContainer;
import org.magic.services.recognition.ImageDesc;
import org.magic.services.recognition.MatchResult;

public class TreeRecogStrat extends AbstractRecognitionStrategy{

	private Node root;
	private ConcurrentLinkedQueue<DescContainer> lq = new ConcurrentLinkedQueue<>();
	private int size=0;


	@Override
	public STATUT getStatut() {
		return STATUT.DEV;
	}
	
	
	
	@Override
	public String getName()
	{
		return "Tree Traversal";
	}


	@Override
	public synchronized void clear()
	{
		root = null;
		size=0;
	}
	
	@Override
	public synchronized void add(DescContainer dc)
	{
		lq.add(dc);
		size++;
	}
	
	@Override
	public int size() {
		return size;
	}

	private synchronized void process(DescContainer id)
	{
		Node current = root;

		if(root == null)
		{
			root = new Node(id);
			return;
		}

		for(;;)
		{
			if(!current.full())
			{
				current.add(new Node(id));
				return;
			}
			else
			{
				current = current.maxHash(id.getDescData());
			}
		}
	}

	@Override
	public MatchResult getMatch(ImageDesc in, double threshhold)
	{

		Node current = root;
		Node maxn = root;
		double max = 0;

		for(;;)
		{
			if(current == null)
			{
				break;
			}
			
			double score = in.compareSURF(current.d.getDescData());
			if(score>max)
			{
				max=score;
				maxn=current;
			}

			else
			{
				Node n = current.max(in);
				if(current.s>max)
				{
					max=current.s;
					maxn=n;
				}
				current = n;
			}
		}
		if(max>threshhold && maxn !=null)
		{
			return new MatchResult(maxn.getD().getStringData(),max);
		}
		return null;
	}

	@Override
	public void finalizeLoad()
	{
		while(!lq.isEmpty())
		{
			process(lq.remove());
		}
	}

	private class Node
	{
		private DescContainer d;
		private Node left;
		private Node right;
		private double s;

		public Node(DescContainer d)
		{
			this.d = d;
		}

		
		public DescContainer getD() {
			return d;
		}
		
		public boolean full()
		{
			return !(left==null || right==null);
		}
		
		public void add(Node n)
		{
			if(left==null)
			{
				left=n;
			}
			else if(right==null)
			{
				right=n;
			}
		}
		
		public Node max(ImageDesc d)
		{
			Node m = null;
			double max = 0;

			if(left != null)
			{
				double score = d.compareSURF(left.d.getDescData());
				if(score>max)
				{
					max=score;
					m = left;
				}
			}
			
			if(right != null)
			{
				double score = d.compareSURF(right.d.getDescData());
				if(score>max)
				{
					max=score;
					m = right;
				}
			}

			s = max;
			return m;
		}

		public Node maxHash(ImageDesc d)
		{
			Node m = null;
			double max = 0;

			if(left != null)
			{
				double score = d.compareHash(left.d.getDescData());
				if(score>max)
				{
					max=score;
					m = left;
				}
			}
			
			if(right != null)
			{
				double score = d.compareHash(right.d.getDescData());
				if(score>max)
				{
					max=score;
					m = right;
				}
			}

			s = max;
			return m;
		}
	}

	
}
