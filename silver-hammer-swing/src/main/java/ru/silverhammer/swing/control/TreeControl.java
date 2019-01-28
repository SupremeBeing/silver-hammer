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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import ru.silverhammer.control.ITree;
import ru.silverhammer.control.ITreeControl;
import ru.silverhammer.control.Tree;

// TODO: consider adding isLeaf
// TODO: disable internal first key navigation
public class TreeControl extends Control<Object, Tree, JTree> implements ITreeControl<Object, Tree, Object> {

	private static final long serialVersionUID = 3020411970292415116L;

	private final Map<Object, DefaultMutableTreeNode> nodes = new HashMap<>();
	private final DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	public TreeControl() {
		super(true);
		getComponent().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		getComponent().getSelectionModel().addTreeSelectionListener(e -> fireValueChanged());
		getComponent().setShowsRootHandles(true);
		getComponent().setModel(new DefaultTreeModel(root));
		getComponent().setRootVisible(false);
		getComponent().addKeyListener(new SearchAdapter() {
			@Override
			// TODO: consider searching starting from current node
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
		if (!isMultiSelection()) {
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
			if (!isMultiSelection()) {
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

	public int getVisibleRowCount() {
		return getComponent().getVisibleRowCount();
	}

	public void setVisibleRowCount(int count) {
		getComponent().setVisibleRowCount(count);
	}

	public boolean isMultiSelection() {
		int mode = getComponent().getSelectionModel().getSelectionMode();
		if (mode == TreeSelectionModel.SINGLE_TREE_SELECTION) {
			return false;
		}
		return true;
	}

	public void setSelectionType(boolean multiSelection) {
		if (multiSelection) {
			getComponent().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		} else {
			getComponent().getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		}
	}

	public Object getSingleSelection() {
		TreePath path = getComponent().getSelectionPath();
		if (path != null) {
			return ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
		}
		return null;
	}

	public Object[] getSelection() {
		List<Object> result = new ArrayList<>();
		TreePath[] paths = getComponent().getSelectionPaths();
		if (paths != null) {
			for (TreePath path : paths) {
				result.add(((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject());
			}
		}
		return result.toArray(new Object[result.size()]);
	}

	public void select(Object value) {
		DefaultMutableTreeNode node = nodes.get(value);
		if (node != null) {
			TreeNode[] path = getModel().getPathToRoot(node);
			getComponent().setSelectionPath(new TreePath(path));
		}
	}

	public void deselect(Object value) {
		DefaultMutableTreeNode node = nodes.get(value);
		if (node != null) {
			TreeNode[] path = getModel().getPathToRoot(node);
			getComponent().removeSelectionPath(new TreePath(path));
		}
	}

	@Override
	protected JTree createComponent() {
		return new JTree();
	}

	@Override
	public ITree<Object> getTree() {
		return new ITree<Object>() {
			@Override
			public void add(Object parent, Object item) {
				DefaultMutableTreeNode parentNode = getNode(parent);
				if (parentNode != null) {
					DefaultMutableTreeNode node = new DefaultMutableTreeNode(item);
					parentNode.add(node);
					nodes.put(item, node);
					getModel().nodeStructureChanged(parentNode);
				}
			}

			@Override
			public void remove(Object parent, int i) {
				DefaultMutableTreeNode parentNode = getNode(parent);
				if (parentNode != null) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) parentNode.getChildAt(i);
					parentNode.remove(node);
					nodes.remove(node.getUserObject());
					getModel().nodeStructureChanged(parentNode);
				}
			}

			@Override
			public Object get(Object parent, int i) {
				DefaultMutableTreeNode parentNode = getNode(parent);
				if (parentNode != null) {
					return ((DefaultMutableTreeNode) parentNode.getChildAt(i)).getUserObject();
				}
				return null;
			}

			@Override
			public int getCount(Object parent) {
				DefaultMutableTreeNode parentNode = getNode(parent);
				if (parentNode != null) {
					return parentNode.getChildCount();
				}
				return -1;
			}

			@Override
			public void clear() {
				nodes.clear();
				root.removeAllChildren();
				getModel().nodeStructureChanged(root);
			}

			@Override
			public Object getParent(Object item) {
				DefaultMutableTreeNode childNode = nodes.get(item);
				return ((DefaultMutableTreeNode) childNode.getParent()).getUserObject();
			}
		};
	}

	public void expand(Object value) {
		DefaultMutableTreeNode node = getNode(value);
		TreeNode[] path = getModel().getPathToRoot(node);
		getComponent().expandPath(new TreePath(path));
	}

	public void collapse(Object value) {
		DefaultMutableTreeNode node = getNode(value);
		TreeNode[] path = getModel().getPathToRoot(node);
		getComponent().collapsePath(new TreePath(path));
	}

	@Override
	public void init(Tree annotation) {
		if (annotation.visibleRows() > 0) {
			setVisibleRowCount(annotation.visibleRows());
		}
		setSelectionType(annotation.multiSelection());
	}
}
