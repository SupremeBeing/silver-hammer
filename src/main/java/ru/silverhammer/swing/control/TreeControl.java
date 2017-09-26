/*
 * Copyright (c) 2017, Dmitriy Shchekotin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package ru.silverhammer.swing.control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ru.silverhammer.core.control.IHierarchyControl;
import ru.silverhammer.core.control.IRowsControl;
import ru.silverhammer.core.control.ISelectionTypeControl;

public class TreeControl extends ValidatableControl<Object, JTree> implements IHierarchyControl<Object, Object>, IRowsControl<Object>, ISelectionTypeControl<Object> {

	private static final long serialVersionUID = 3020411970292415116L;

	private final Map<Object, DefaultMutableTreeNode> nodes = new HashMap<>();
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	public TreeControl() {
		super(true);
		getComponent().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		getComponent().getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				fireValueChanged();
			}
		});
		getComponent().setShowsRootHandles(true);
		getComponent().setModel(new DefaultTreeModel(root));
		getComponent().setRootVisible(false);
		getComponent().addKeyListener(new SearchAdapter() {
			@Override
			protected void search(String search) {
				DefaultMutableTreeNode node = findMatchingObject(root, search);
				if (node != null) {
					TreeNode[] nodes = getModel().getPathToRoot(node);
					TreePath path = new TreePath(nodes);
					getComponent().expandPath(path);
					getComponent().setSelectionPath(path);
					getComponent().scrollPathToVisible(path);
				}
			}
		});
	}

	private DefaultMutableTreeNode findMatchingObject(DefaultMutableTreeNode root, String search) {
		int count = getModel().getChildCount(root);
		for (int i = 0; i < count; i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) getModel().getChild(root, i);
			Object data = child.getUserObject();
			if (data != null && data.toString().contains(search)) {
				return child;
			}
		}
		return null;
	}

	private DefaultTreeModel getModel() {
		return (DefaultTreeModel) getComponent().getModel();
	}
	
	private DefaultMutableTreeNode getNode(Object value) {
		return value == null ? root : nodes.get(value);
	}

	@Override
	public Object getValue() {
		if (getSelectionType() == SelectionType.Single) {
			TreePath path = getComponent().getSelectionPath();
			if (path != null) {
				return ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
			}
		} else {
			List<Object> result = new ArrayList<>();
			TreePath[] paths = getComponent().getSelectionPaths();
			if (paths != null) {
				for (TreePath path : paths) {
					result.add(((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject());
				}
			}
			return result;
		}
		return null;
	}

	@Override
	public void setValue(Object value) {
		getComponent().clearSelection();
		if (value != null) {
			if (getSelectionType() == SelectionType.Single) {
				DefaultMutableTreeNode node = nodes.get(value);
				if (node != null) {
					TreeNode[] path = getModel().getPathToRoot(node);
					getComponent().setSelectionPath(new TreePath(path));
				}
			} else if (value instanceof Collection) {
				for (Object o : (Collection<?>) value) {
					DefaultMutableTreeNode node = nodes.get(o);
					TreeNode[] path = getModel().getPathToRoot(node);
					getComponent().addSelectionPath(new TreePath(path));
				}
			}
		}
	}

	@Override
	public int getVisibleRowCount() {
		return getComponent().getVisibleRowCount();
	}

	@Override
	public void setVisibleRowCount(int count) {
		getComponent().setVisibleRowCount(count);
	}

	@Override
	public SelectionType getSelectionType() {
		int mode = getComponent().getSelectionModel().getSelectionMode();
		if (mode == TreeSelectionModel.SINGLE_TREE_SELECTION) {
			return SelectionType.Single;
		} else if (mode == TreeSelectionModel.CONTIGUOUS_TREE_SELECTION) {
			return SelectionType.Interval;
		} else if (mode == TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION) {
			return SelectionType.Multi;
		}
		return null;
	}

	@Override
	public void setSelectionType(SelectionType mode) {
		if (mode == SelectionType.Single) {
			getComponent().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		} else if (mode == SelectionType.Interval) {
			getComponent().getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
		} else if (mode == SelectionType.Multi) {
			getComponent().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		}
	}

	@Override
	protected JTree createComponent() {
		return new JTree();
	}

	@Override
	public void addItem(Object parent, Object item) {
		DefaultMutableTreeNode parentNode = getNode(parent);
		if (parentNode != null) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
			parentNode.add(node);
			nodes.put(item, node);
			getModel().nodeStructureChanged(parentNode);
		}
	}

	@Override
	public void removeItem(Object item) {
		DefaultMutableTreeNode node = nodes.get(item);
		if (node != null) {
			DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
			parent.remove(node);
			nodes.remove(item);
			getModel().nodeStructureChanged(parent);
		}
	}

	@Override
	public void addItem(Object parent, int i, Object item) {
		if (item != null) {
			DefaultMutableTreeNode parentNode = getNode(parent);
			if (parentNode != null) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
				parentNode.insert(node, i);
				nodes.put(item, node);
				getModel().nodeStructureChanged(parentNode);
			}
		}
	}

	@Override
	public void setItem(Object parent, int i, Object item) {
		if (item != null) {
			DefaultMutableTreeNode parentNode = getNode(parent);
			if (parentNode != null) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) parentNode.getChildAt(i);
				node.setUserObject(item);
				getModel().nodeStructureChanged(parentNode);
			}
		}
	}

	@Override
	public void removeItem(Object parent, int i) {
		DefaultMutableTreeNode parentNode = getNode(parent);
		if (parentNode != null) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) parentNode.getChildAt(i);
			parentNode.remove(node);
			nodes.remove(node.getUserObject());
			getModel().nodeStructureChanged(parentNode);
		}
	}

	@Override
	public Object getItem(Object parent, int i) {
		DefaultMutableTreeNode parentNode = getNode(parent);
		if (parentNode != null) {
			return ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
		}
		return null;
	}

	@Override
	public int getItemCount(Object parent) {
		DefaultMutableTreeNode parentNode = getNode(parent);
		if (parentNode != null) {
			return parentNode.getChildCount();
		}
		return -1;
	}

	@Override
	public void clearItems() {
		nodes.clear();
		root.removeAllChildren();
		getModel().nodeStructureChanged(root);
	}

	@Override
	public void expand(Object value) {
		DefaultMutableTreeNode node = getNode(value);
		TreeNode[] path = getModel().getPathToRoot(node);
		getComponent().expandPath(new TreePath(path));
	}

	@Override
	public void collapse(Object value) {
		DefaultMutableTreeNode node = getNode(value);
		TreeNode[] path = getModel().getPathToRoot(node);
		getComponent().collapsePath(new TreePath(path));
	}

	@Override
	public Object getParent(Object child) {
		DefaultMutableTreeNode childNode = nodes.get(child);
		return ((DefaultMutableTreeNode) childNode.getParent()).getUserObject();
	}
}
