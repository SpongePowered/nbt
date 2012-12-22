/*
 * This file is part of SimpleNBT.
 *
 * Copyright (c) 2011, SpoutDev <http://www.spout.org/>
 * SimpleNBT is licensed under the SpoutDev License Version 1.
 *
 * SimpleNBT is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * SimpleNBT is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.nbt.gui;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;

import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.CompoundMap;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.IntArrayTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.ShortArrayTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;

public class NBTViewer extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;
	private String format = "";
	private JTree tree;
	private DefaultMutableTreeNode top;

	public NBTViewer() {
		
		JMenuBar menu = new JMenuBar();
        setJMenuBar(menu);

        JMenu file = new JMenu("File");
        
        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(this);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(this);

        file.add(open);
        file.addSeparator();
        file.add(exit);

        menu.add(file);
        
        top = new DefaultMutableTreeNode("NBT Contents");
        
        tree = new JTree(top);
        
        JScrollPane treeView = new JScrollPane(tree);
        
        add(treeView);
		
		setTitle("SimpleNBT Viewer");
		setSize(300, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	NBTViewer viewer = new NBTViewer();
                viewer.setVisible(true);
            }
        });
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		if (command == null) {
			return;
		} else if (command.equals("Open")) {
			openFile();
		} else if (command.equals("Exit")) {
			System.exit(0);
		}

	}
	
	private void openFile() {
		FileDialog d = new FileDialog(this, "Open File", FileDialog.LOAD);
		d.setVisible(true);
		File dir = new File(d.getDirectory());
		File f = new File(dir, d.getFile());
		List<Tag<?>> tags = readFile(f);
		updateTree(tags);
		top.setUserObject("NBT Contents [" + format + "]");
		((DefaultTreeModel) tree.getModel()).nodeChanged(top);
	}
	
	private List<Tag<?>> readFile(File f) {
		List<Tag<?>> tags = readRawNBT(f, true);
		if (tags != null) {
			format = "Compressed NBT";
			return tags;
		}
		tags = readRawNBT(f, false);
		if (tags != null) {
			format = "Uncompressed NBT";
			return tags;
		}
		
		format = "Unknown";
		return null;
	}
	
	private List<Tag<?>> readRawNBT(File f, boolean compressed) {
		List<Tag<?>> tags = new ArrayList<Tag<?>>();
		try {
			InputStream is = new FileInputStream(f);
			NBTInputStream ns = new NBTInputStream(is, compressed);
			try {
				boolean eof = false;
				while (!eof) {
					try {
						tags.add(ns.readTag());
					} catch (EOFException e) {
						eof = true;
					}
				}
			} finally {
				try {
					ns.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(this, "Unable to close file", "File Read Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Unable to open file", "File Read Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e) {
			return null;
		}
		return tags;
	}
	
	private void updateTree(List<Tag<?>> tags) {

		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();  
		
		int children = top.getChildCount();
		for (int i = 0; i < children; i++) {
			model.removeNodeFromParent((MutableTreeNode) top.getChildAt(i));
		}
		
		if (tags == null) {
			return;
		}

		if (tags.size() == 1) {
			model.insertNodeInto(getNode(tags.get(0)), top, 0);
			tree.expandRow(0);
		} else {
			int i = 0;
			for (Tag<?> t : tags) {
				DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root Tag " + i);
				model.insertNodeInto(root, top, i);
				model.insertNodeInto(getNode(t), root, 0);
				tree.expandRow(i);
				i++;
			}
		}
	}
	
	private static DefaultMutableTreeNode getNode(Tag<?> tag) {
		return getNode(tag, true);
	}
	
	@SuppressWarnings("unchecked")
	private static DefaultMutableTreeNode getNode(Tag<?> tag, boolean includeName) {
		if (tag instanceof CompoundTag) {
			return getNode((CompoundTag) tag);
		} else if (tag instanceof ListTag<?>) {
			try {
				return getNode((ListTag<Tag<?>>) tag);
			} catch (ClassCastException e) {
			}
		} else if (tag instanceof ByteArrayTag) {
			return getNode((ByteArrayTag) tag);
		} else if (tag instanceof ShortArrayTag) {
			return getNode((ShortArrayTag) tag);
		} else if (tag instanceof IntArrayTag) {
			return getNode((IntArrayTag) tag);
		}
		String message = includeName ? (tag.getName() + ":" + tag.getValue()) : tag.getValue().toString();
		return new DefaultMutableTreeNode(message);
	}
	
	private static DefaultMutableTreeNode getNode(CompoundTag tag) {
		CompoundMap map = tag.getValue();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(tag.getName() + " [Map]");
		for (Tag<?> t : map.values()) {
			DefaultMutableTreeNode child = getNode(t);
			root.add(child);
		}
		return root;
	}
	
	private static DefaultMutableTreeNode getNode(ListTag<Tag<?>> tag) {
		List<Tag<?>> values = tag.getValue();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(tag.getName() + " [List]");
		for (Tag<?> t : values) {
			DefaultMutableTreeNode child = getNode(t, false);
			root.add(child);
		}
		return root;
	}
	
	private static DefaultMutableTreeNode getNode(ByteArrayTag tag) {
		byte[] values = tag.getValue();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(tag.getName() + " [byte[]]");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (byte b : values) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(b);
		}
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(sb.toString());
		root.add(child);
		return root;
	}
	
	private static DefaultMutableTreeNode getNode(ShortArrayTag tag) {
		short[] values = tag.getValue();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(tag.getName() + " [short[]]");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int i : values) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(i);
		}
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(sb.toString());
		root.add(child);
		return root;
	}

	private static DefaultMutableTreeNode getNode(IntArrayTag tag) {
		int[] values = tag.getValue();
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(tag.getName() + " [int[]]");
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (int i : values) {
			if (!first) {
				sb.append(", ");
			} else {
				first = false;
			}
			sb.append(i);
		}
		DefaultMutableTreeNode child = new DefaultMutableTreeNode(sb.toString());
		root.add(child);
		return root;
	}
	
}
